package com.example.depositomodaintima.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.depositomodaintima.R;
import com.example.depositomodaintima.helper.ConfiguracaoFirebase;
import com.example.depositomodaintima.model.Usuario;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class CadastroActivity extends AppCompatActivity {

    private EditText nomeCadastro;
    private EditText cpfCadastro;
    private EditText emailCadastro;
    private EditText senhaCadastro;
    private EditText confirSenhaCadastro;
    private Button botaoCadastro;
    private ProgressBar progressBarCadatro;
    private Usuario usuario;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        initAtributesCadastro();

        //Cadastrar Usuario
        botaoCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textoNome = nomeCadastro.getText().toString();
                String textoCPF = cpfCadastro.getText().toString();
                String textoEmal = emailCadastro.getText().toString();
                String textoSenha = senhaCadastro.getText().toString();
                String textoConfirSenha = confirSenhaCadastro.getText().toString();

                //Verificação de os campos digitados estão em brancos
                if(!textoNome.isEmpty()){
                    if(!textoCPF.isEmpty()){
                        if(!textoEmal.isEmpty()){
                            if(!textoSenha.isEmpty()){
                                if(!textoConfirSenha.isEmpty()){
                                    //Verificação se a senha está igual a contra-senha
                                    if(textoSenha.equals(textoConfirSenha)){

                                        progressBarCadatro.setVisibility(View.VISIBLE);
                                        
                                        //inicialização do usuário
                                        usuario = new Usuario();
                                        usuario.setNome(textoNome);
                                        usuario.setCpf(textoCPF);
                                        usuario.setEmail(textoEmal);
                                        usuario.setSenha(textoSenha);
                                        usuario.setVendendo("NAO");

                                        //Inserir dados no FirebaseAuth
                                        cadastrarUsuario(usuario);




                                    }
                                    else{
                                        Toast.makeText(CadastroActivity.this,
                                                "A confirmação de senha deve ser igual a senha!",
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                                else{
                                    Toast.makeText(CadastroActivity.this,
                                            "Preencha a confirmação da senha.",
                                            Toast.LENGTH_SHORT).show();
                                }

                            }
                            else{
                                Toast.makeText(CadastroActivity.this,
                                        "Preencha a senha.",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }
                        else{
                            Toast.makeText(CadastroActivity.this,
                                    "Preencha o Email.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                    else{
                        Toast.makeText(CadastroActivity.this,
                                "Preencha o CPF.",
                                Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    Toast.makeText(CadastroActivity.this,
                            "Preencha o nome.",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    //Inicialização Atributos
    public void initAtributesCadastro(){
        nomeCadastro = (EditText) findViewById(R.id.idNomeCadastro);
        cpfCadastro = (EditText) findViewById(R.id.idCPFCadastro);
        emailCadastro = (EditText) findViewById(R.id.idEmailCadatro);
        senhaCadastro = (EditText) findViewById(R.id.idSenhaCadatro);
        confirSenhaCadastro = (EditText) findViewById(R.id.idConfirmacaoSenhaCadatro);
        botaoCadastro = (Button) findViewById(R.id.idBotaoCadastrar);
        progressBarCadatro = (ProgressBar) findViewById(R.id.idProgressBarCadastro);

        //Máscara para CPF NNN.NNN.NNN-NN
        SimpleMaskFormatter simpleMaskCPF = new SimpleMaskFormatter("NNN.NNN.NNN-NN");
        MaskTextWatcher maskCPF = new MaskTextWatcher(cpfCadastro,simpleMaskCPF);
        cpfCadastro.addTextChangedListener(maskCPF);


    }

    //Cadastrar Usuário no Firebase e fazer as validações
    public void cadastrarUsuario(final Usuario usuario){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(
                this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            Toast.makeText(CadastroActivity.this,
                                    "Usuário cadastrado com SUCESSO!",
                                    Toast.LENGTH_SHORT).show();
                            usuario.setId(ConfiguracaoFirebase.getIdUsuario());
                            usuario.salvar();
                            startActivity(new Intent(getApplicationContext(), TelaAguardandoActivity.class));
                            finish();

                        }
                        else{
                            String erroExcecao = "";
                            try {
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e) {
                                erroExcecao = "Digite uma SENHA mais forte";
                                senhaCadastro.setText("");
                                confirSenhaCadastro.setText("");
                            }catch (FirebaseAuthInvalidCredentialsException e) {
                                erroExcecao = "Digite um EMAIL válido!";
                                emailCadastro.setText("");
                            }catch (FirebaseAuthUserCollisionException e) {
                                erroExcecao = "Este EMAIL já foi utilizado!";
                                emailCadastro.setText("");
                            } catch (Exception e) {
                                erroExcecao = "Ao cadastrar o usuário: " + e.getMessage();
                                e.printStackTrace();
                            }
                            Toast.makeText(CadastroActivity.this,
                                    erroExcecao,
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        );
    }//fim método cadastrar Usuario
}