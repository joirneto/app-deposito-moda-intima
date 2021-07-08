package com.example.depositomodaintima.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.depositomodaintima.R;
import com.example.depositomodaintima.helper.ConfiguracaoFirebase;
import com.example.depositomodaintima.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText emailLogin;
    private EditText senhaLogin;
    private Button botaoLogin;
    private ProgressBar progressBarLogin;
    private Usuario usuario;

    private FirebaseAuth autenticacao;

    private DatabaseReference ativacaoRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Verificando se algum usuário está logado
      //   verificarUsuarioLogado();

        //Inicializando os Atributos
        initAtributesLogin();

        botaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoEmail = emailLogin.getText().toString();
                String textoSenha = senhaLogin.getText().toString();
                if(!textoEmail.isEmpty()){
                    if(!textoSenha.isEmpty()){
                        usuario = new Usuario();
                        usuario.setEmail(textoEmail);
                        usuario.setSenha(textoSenha);
                        validarLogin(usuario);

                    }else{
                        Toast.makeText(LoginActivity.this,
                                "Digite a SENHA!",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(LoginActivity.this,
                            "Preencha o email!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Função para abrir a tela de cadastro de usuário quando clicar em Cadastre-se
    public void abrirCadastroNovoUsuario(View view){
        Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
        startActivity(intent);
    }

    //Metodo iniciar atributos
    public void initAtributesLogin(){
        emailLogin = findViewById(R.id.idEmailLogin);
        senhaLogin = findViewById(R.id.idSenhaLogin);
        botaoLogin = findViewById(R.id.idBotaoLogin);
        progressBarLogin = findViewById(R.id.idProgressBarLogin);
    }

    //Método Verificar Usuário Logado
   public void verificarUsuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if(autenticacao.getCurrentUser()!=null){
            ativacaoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("usuarios")
                    .child(ConfiguracaoFirebase.getIdUsuario());
            ativacaoRef.child("ativado").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String atv;
                    atv = String.valueOf(dataSnapshot.getValue());

                    if (atv.equals("SIM")){
                        startActivity(new Intent(getApplicationContext(),Main2Activity.class));
                        finish();
                    }
                    else {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }//Fim verificarUsuarioLogado


    //Método para validar login no Firebase
    public void validarLogin(Usuario usuario){
        progressBarLogin.setVisibility(View.VISIBLE);
        autenticacao= ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressBarLogin.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this,
                            "Login Efetuado com sucesso!",
                            Toast.LENGTH_SHORT).show();

                    ativacaoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                            .child("usuarios")
                            .child(ConfiguracaoFirebase.getIdUsuario());
                    ativacaoRef.child("ativado").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String atv;
                            atv = String.valueOf(dataSnapshot.getValue());

                            if (atv.equals("SIM")){
                                startActivity(new Intent(getApplicationContext(),Main2Activity.class));
                                finish();
                            }
                            else {
                                startActivity(new Intent(getApplicationContext(), TelaAtivacaoUserActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }else{
                    progressBarLogin.setVisibility(View.GONE);
                    String erroExcecao = "";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e) {
                        erroExcecao = "Email de usuário não existe! Digite um email válido!";
                        emailLogin.setText("");
                        senhaLogin.setText("");
                    }catch (FirebaseAuthInvalidCredentialsException e) {
                        erroExcecao = "Senha errada. Favor digitar senha correta!";
                        senhaLogin.setText("");
                    }catch (Exception e) {
                        erroExcecao = "Ao logar o usuário: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this,
                            erroExcecao,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }//Fim validarLogin

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();;
    }
}

/*progressBarLogin.setVisibility(View.VISIBLE);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }else{

                    String erroExcecao = "";
                    try {
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException e) {
                        erroExcecao = "Email de usuário não existe! Digite um email válido!";
                        emailLogin.setText("");
                        senhaLogin.setText("");
                    }catch (FirebaseAuthInvalidCredentialsException e) {
                        erroExcecao = "Senha errada. Favor digitar senha correta!";
                        senhaLogin.setText("");
                    }catch (Exception e) {
                        erroExcecao = "Ao logar o usuário: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this,
                            erroExcecao,
                            Toast.LENGTH_SHORT).show();

                }

            }//Fim OnCompleteListener<AuthResult>
        });*/
