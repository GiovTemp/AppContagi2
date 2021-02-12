package it.gadg.contagiapp.evento;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import it.gadg.contagiapp.R;
import it.gadg.contagiapp.modelli.ListaEvento;

public class ListaEventiActivity extends AppCompatActivity {

    private FirebaseAuth mAuth; //dichiaro variabile per l'auenticazione firebase
    FirebaseFirestore db;
    ListView listView;//dichiaro la listview
    String[] idEventi;//array che conterrà gli id degli eventi
    String[] ruoli;//array che conterrà i ruoli dell'utente per gli eventi
    String[] nomi;//array che conterrà i nomi degli eventi
    ArrayList<ListaEvento> eventi = new ArrayList<>();//array che conterrà tutti gli oggetti eventi
    int flag;//falg che conterrà il numero di eventi
    TextView Neventi;//textView del plurals

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_eventi);

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));


        mAuth= FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        Neventi=findViewById(R.id.Neventi);

        //estrapoliamo dal DB tutte le partecipazioni attive
        db.collection("PartecipazioneEvento").whereEqualTo("UID", user.getUid()).whereEqualTo("status", 1).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                flag=queryDocumentSnapshots.size();//ricaviamo il numero di documenti estrapolati
                if(flag>0){
                    //impostiamo la stringa plurals
                    Resources res = getApplicationContext().getResources();
                    Neventi.setText(res.getQuantityString(R.plurals.Neventi,flag,flag));

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                        String id = document.getString("idEvento");//ricaviamo id evento
                        String ruolo = document.getString("role");//ricaviamo ruolo
                        final ListaEvento x = new ListaEvento(id,ruolo);//creaimo oggetto su misura per la lsita

                        //ricaviamo le restanti informazioni sull'evento
                        db.collection("Eventi").document((String) document.get("idEvento")).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document != null) {
                                        //salviamo i dati recuperati
                                        x.nome=document.getString("nome");
                                        x.luogo=document.getString("nomeLuogo");
                                        x.data=document.getString("data");
                                        x.ora = document.getString("oraInizio");
                                        //confermiamo l'evento
                                        salvaEvento(x);

                                    }
                                } else {
                                    Log.d("Errore", "get failed with ", task.getException());
                                }
                            }

                        });
                    }
                }else{
                    setContentView(R.layout.no_eventi);
                }

            }

            private void salvaEvento(ListaEvento x) {
                eventi.add(x);//aggiungiamo l'vento alla lista degli eventi

                //se abbiamo confermato tutti gli eventi
                if(eventi.size()==flag){
                    idEventi = new String[eventi.size()];
                    for (int j = 0; j < eventi.size(); j++) {
                        idEventi[j] = eventi.get(j).id;
                    }

                    nomi = new String[eventi.size()];
                    for (int j = 0; j < eventi.size(); j++) {
                        nomi[j] = eventi.get(j).nome;
                    }

                    ruoli = new String[eventi.size()];
                    for (int j = 0; j < eventi.size(); j++) {
                        if(eventi.get(j).ruolo.equals("1")){
                            ruoli[j] = getString(R.string.admin);
                        }else{
                            ruoli[j] = getString(R.string.partecipante);
                        }

                    }

                    listView = findViewById(R.id.listaEventi);
                    ListaEventiActivity.Adapter adapter = new ListaEventiActivity.Adapter(getApplicationContext(),nomi,ruoli,idEventi);//chiamo l'adpter per la lista
                    listView.setAdapter(adapter);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if(eventi.get(position).ruolo.equals("1")){
                                Intent intent = new Intent(getApplicationContext(), GestisciEvento.class);
                                intent.putExtra("NomeEvento",eventi.get(position).nome);
                                intent.putExtra("LuogoEvento",eventi.get(position).luogo);
                                intent.putExtra("DataEvento",eventi.get(position).data);
                                intent.putExtra("OraEvento",eventi.get(position).ora);
                                intent.putExtra("idEvento",eventi.get(position).id);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                            }else{
                                Intent intent = new Intent(getApplicationContext(), VisualizzaEventoActivity.class);
                                intent.putExtra("NomeEvento",eventi.get(position).nome);
                                intent.putExtra("LuogoEvento",eventi.get(position).luogo);
                                intent.putExtra("DataEvento",eventi.get(position).data);
                                intent.putExtra("OraEvento",eventi.get(position).ora);
                                intent.putExtra("idEvento",eventi.get(position).id);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                            }


                        }
                    });

                }
            }
        });

    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    class Adapter extends ArrayAdapter<String> {
        Context context;
        String NomiEventi[];
        String RuoliEventi[];
        String IdEventi[];

        Adapter (Context c,String NomiEventi[] , String RuoliEventi[], String IdEventi[]){
            super(c,R.layout.rigagruppo,R.id.NomeMembro,NomiEventi);
            this.context = c;
            this.NomiEventi=NomiEventi;
            this.RuoliEventi=RuoliEventi;
            this.IdEventi=IdEventi;


        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rigaevento = layoutInflater.inflate(R.layout.rigaevento,parent,false);
            TextView nomeEventoRiga = rigaevento.findViewById(R.id.NomeEvento);
            TextView ruoloEventoRiga = rigaevento.findViewById(R.id.RuoloEvento);
            TextView idEventoRiga = rigaevento.findViewById(R.id.idEvento);

            nomeEventoRiga.setText(NomiEventi[position]);
            ruoloEventoRiga.setText(RuoliEventi[position]);
            idEventoRiga.setText(IdEventi[position]);


            return rigaevento;
        }
    }
}