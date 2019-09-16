package com.futureapp.studyground;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SinginActivity extends AppCompatActivity {

    private EditText txtemail;
    private EditText txtpwd;
    private EditText txtcPwd;
    private EditText txtname;
    private Button buttonRegistro;
    private Button buttonReturn;
    private Spinner spinPrograma;
    private Spinner spinUniversidad;
    private Button buttonMaterias;

    //Datos a registrar
    private String name = "";
    private String email = "";
    private String pwd = "";
    private String cpwd = "";
    private String univ = "";
    private String programa = "";

    // Variables firebase
    FirebaseAuth auth;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singin);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Resources res = getResources();



        //Cargar archivo xml


        //-------------------------------------------------------------------------------------------

        //instacia firebase auth
       auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();

        txtemail = (EditText) findViewById(R.id.txtemail);
        txtpwd = (EditText) findViewById(R.id.txtpwd);
        txtcPwd = (EditText) findViewById(R.id.txtcPwd);
        txtname = (EditText) findViewById(R.id.txtname);
        buttonRegistro = (Button) findViewById(R.id.buttonRegistro);
        buttonReturn = (Button) findViewById(R.id.buttonReturn);
        spinUniversidad = (Spinner) findViewById(R.id.spinuniversidad);
        spinPrograma = (Spinner) findViewById(R.id.spinprograma);
        buttonMaterias = (Button) findViewById(R.id.buttonMaterias);






        spinPrograma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                programa = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinUniversidad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                univ = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




      final String[] materias = res.getStringArray(R.array.mCienciasBasicas);

      buttonMaterias.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              if(!programa.isEmpty()){
                  System.out.println("Programa btn"+programa);
                  String[] materiasp=EleccionPrograma(programa);


                  List allMaterias=new ArrayList();


                  for (int i=0;i<materias.length;i++){
                      allMaterias.add(materias[i]);
                  }


                  for (int i=0;i<materiasp.length;i++){
                      allMaterias.add(materiasp[i]);
                      System.out.println(i+" progra:" +materiasp[i]);
                  }

                  for (int i=0;i<allMaterias.size();i++){
                      System.out.println(i+"programa materias cb:"+allMaterias.get(i).toString());
                  }

              }
          }
      });




        buttonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = txtemail.getText().toString();
                pwd = txtpwd.getText().toString();
                cpwd = txtcPwd.getText().toString();
                name = txtname.getText().toString();

                if (!name.isEmpty() && !email.isEmpty() && !pwd.isEmpty() && !cpwd.isEmpty() && !programa.isEmpty() && !univ.isEmpty()) {
                    if (pwd.length() >= 6) {
                        if (pwd.equals(cpwd)) {
                            registerUser();
                        } else {
                            Toast.makeText(SinginActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(SinginActivity.this, "La contraseña debe contener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SinginActivity.this, "Debes completar los campos", Toast.LENGTH_SHORT).show();
                }

            }
        });

        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SinginActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void registerUser() {
        auth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {


                    //Lista con valores para add to firebase
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", name);
                    map.put("email", email);
                    map.put("pwd", pwd);
                    map.put("programa", programa);
                    map.put("universidad", univ);

                    String id = auth.getCurrentUser().getUid();

                    db.child("Users").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()) {
                                Toast.makeText(SinginActivity.this, "Datos subidos correctamente", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SinginActivity.this, ProfileMainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(SinginActivity.this, "No se pudieron crear los datos correctamente", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    Toast.makeText(SinginActivity.this, "No se pudo registrar este usuario", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private String[] EleccionPrograma(String programa){
        Resources res=getResources();

        String[] materiasp=new String[35];
        switch (programa){
            case "Ingeniería de Sistemas":
                materiasp=res.getStringArray(R.array.mSistemas);
                break;
            case "Ingeniería Civil":
                materiasp=res.getStringArray(R.array.mCivil);
                break;
            case "Ingeniería Administrativa":
                materiasp=res.getStringArray(R.array.mAdministrativa);
                break;
            case "Ingeniería Financiera":
                materiasp=res.getStringArray(R.array.mFinanciera);
                break;
            case "Ingeniería Geologica":
                materiasp=res.getStringArray(R.array.mGeologica);
                break;
            case "Ingeniería Industrial":
                materiasp=res.getStringArray(R.array.mIndustrial);
                break;
            case "Ingeniería Mecanica":
                materiasp=res.getStringArray(R.array.mMecanica);
                break;
            case "Ingeniería Mecatronica":
                materiasp=res.getStringArray(R.array.mMecatronica);
                break;
            case "Ingeniería Biomedica":
                materiasp=res.getStringArray(R.array.mBiomedica);
                break;
            case "Ingeniería Ambiental":
                materiasp=res.getStringArray(R.array.mAmbiental);
                break;
            case "Fisica":
                materiasp=res.getStringArray(R.array.mSistemas);
                break;
        }
        return materiasp;
    }
}
