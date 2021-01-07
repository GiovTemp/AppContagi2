package it.gadg.contagiapp.modelli;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Gruppo {
    public String Nome;

    public Gruppo(){};

    public Gruppo(String Nome){
        this.Nome = Nome;
    }

}
