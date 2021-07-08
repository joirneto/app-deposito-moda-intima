package com.example.depositomodaintimacliente.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.depositomodaintimacliente.R;
import com.example.depositomodaintimacliente.helper.ConfiguracaoFirebase;
import com.example.depositomodaintimacliente.model.Venda;
import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class RegistrarDadosClienteEntregaActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Button registrarEndereco;
    private EditText observacoes, nome, cidade,estado;
    private TextView contato;
    private Venda vendaAux;
    private String endereco, nomeCliente, contatoCliente, cidadeCliente, estadoCliente;
    private DatabaseReference salvarDadosRef;
    private BottomNavigationViewEx bottomNavigationViewExRDC;
    private AlertDialog.Builder dialog;


    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_dados_cliente_entrega);

        initAtributesDadosEndereco();
        contato.setEnabled(false);


        //Configuração Buttom Navigation
        configuracaoBottomNavigation();


        if (!recupTextoDadosCliente().isEmpty()){
            String dadosGerais = recupTextoDadosCliente();
            String[] dadosAux = dadosGerais.split("//");
            nome.setText(dadosAux[0]);
            cidade.setText(dadosAux[2]);
            estado.setText(dadosAux[3]);

        }



        vendaAux = (Venda) getIntent().getSerializableExtra("vendaSelecionada");
        if(!vendaAux.getEnderecoEntrega().equals("")) {
            observacoes.setText(vendaAux.getEnderecoEntrega());
        }


        //-------------------------------Obter numero do chip e solicitar ao usuario para confirmar ----------
         mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();

         obterNumeroAtual();

        //-------------------------------FIM Obter numero do chip e solicitar ao usuario para confirmar ----------


    }

    public void initAtributesDadosEndereco() {
        registrarEndereco = findViewById(R.id.idBotaoGravarEndereco);
        nome = findViewById(R.id.idNomeCliente);
        contato = findViewById(R.id.idContatoCliente);
        observacoes = findViewById(R.id.idEnderecoVendaRealizadaOBS);
        cidade = findViewById(R.id.idCidadeCliente);
        estado = findViewById(R.id.idEstadoCliente);
        bottomNavigationViewExRDC = findViewById(R.id.idBottomNavigationTabRegistrarDados);

        //Máscara para ZAP (NN) N NNNN - NNNN
        SimpleMaskFormatter simpleMaskZAP = new SimpleMaskFormatter("(NN) N NNNN - NNNN");
        MaskTextWatcher maskZAP = new MaskTextWatcher(contato,simpleMaskZAP);
        contato.addTextChangedListener(maskZAP);

    }

    public void gravarDados(View view) {
        endereco = observacoes.getText().toString().toUpperCase();
        nomeCliente = nome.getText().toString().toUpperCase();
        contatoCliente= contato.getText().toString().toUpperCase();
        cidadeCliente = cidade.getText().toString().toUpperCase();
        estadoCliente = estado.getText().toString().toUpperCase();

        if(!nomeCliente.isEmpty()){
            if(!contatoCliente.isEmpty() && (contatoCliente.length() == 18)){
                if (!cidadeCliente.isEmpty()){
                    if(!estadoCliente.isEmpty()){
                        if(!endereco.isEmpty()){
                            salvarDadosClienteEntrega();
                            Intent intent = new Intent(getApplicationContext(),ConfirmacaoPedidoActivity.class);
                            intent.putExtra("vendaSelecionada", vendaAux);
                            startActivity(intent);
                            finish();
                        }else{
                            Toast.makeText(this,
                                    "Informe o endereço de entrega ou Retirada em loja",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }else{
                        Toast.makeText(this,
                                "Digite seu ESTADO!",
                                Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(this,
                            "Digite sua CIDADE!",
                            Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(this,
                        "Digite um número de telefone com válido!",
                        Toast.LENGTH_SHORT).show();

            }

        }else{
            Toast.makeText(this,
                    "Digite o seu NOME!",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void salvarDadosClienteEntrega(){
        vendaAux.setCliente(nomeCliente);
        vendaAux.setEnderecoEntrega(endereco);
        vendaAux.setCidadeCliente(cidadeCliente);
        vendaAux.setEstadoCliente(estadoCliente);
        vendaAux.setContatoCliente(contatoCliente);

        gravarArquivoDadosCliente(nomeCliente+"//"+contatoCliente+"//"+cidadeCliente+"//"+estadoCliente);

        salvarDadosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("vendasClientesGeral")
                .child(vendaAux.getIdVenda());
        salvarDadosRef.setValue(vendaAux);
    }

    private void gravarArquivoDadosCliente(String idVenda){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("dadosCliente.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(idVenda);
            outputStreamWriter.close();
        }catch (IOException e){

        }
    }

    private String recupTextoDadosCliente(){
        String resultado = "";

        try {
            InputStream arquivo = openFileInput("dadosCliente.txt");
            if(arquivo!=null){
                InputStreamReader inputStreamReader = new InputStreamReader(arquivo);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String linha = "";
                while ((linha = bufferedReader.readLine())!=null){
                    resultado += linha;
                }

                arquivo.close();
            }

        }catch (IOException e){


        }

        return resultado;
    }
    private String recupTextoDadosClienteRDC(){
        String resultado = "";

        try {
            InputStream arquivo = openFileInput("dadosCliente.txt");
            if(arquivo!=null){
                InputStreamReader inputStreamReader = new InputStreamReader(arquivo);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String linha = "";
                while ((linha = bufferedReader.readLine())!=null){
                    resultado += linha;
                }

                arquivo.close();
            }

        }catch (IOException e){


        }

        return resultado;
    }

    //Método para Configurar Bottom Navigation
    private void configuracaoBottomNavigation(){
        bottomNavigationViewExRDC.enableAnimation(false);
        bottomNavigationViewExRDC.enableItemShiftingMode(true);
        bottomNavigationViewExRDC.enableShiftingMode(false);
        bottomNavigationViewExRDC.setTextVisibility(true);
        habilitarNavegacaoCliente(bottomNavigationViewExRDC);

    }//fim configuracaoBottomNavigation

    //Método para Configurar Eventos de Click Bottom Navigation para Clientes
    private void habilitarNavegacaoCliente(BottomNavigationViewEx viewEx){
        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.ic_home:
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        return  true;
                    case R.id.ic_minhas_compras:
                        if(recupTextoDadosClienteRDC().isEmpty()){
                            Toast.makeText(getApplicationContext(),
                                    "Sem Histórico de Compras",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            startActivity(new Intent(getApplicationContext(),MinhasComprasActivity.class));
                        }
                        return  true;
                    case R.id.ic_menu:
                        startActivity(new Intent(getApplicationContext(),MenuActivity.class));
                        return  true;
                }

                return false;
            }
        });
    }//fim habilitarNavegacaoAdmin

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


//-------------------------------Obter numero do chip e solicitar ao usuario para confirmar ----------

    private void obterNumeroAtual(){
        HintRequest hintRequest = new HintRequest.Builder()
                .setHintPickerConfig(new CredentialPickerConfig.Builder().setShowCancelButton(false).build())
                .setPhoneNumberIdentifierSupported(true).build();

        PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(mGoogleApiClient,hintRequest);

        try {
            startIntentSenderForResult(intent.getIntentSender(),123,null,0,0,0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 123) {
            if (resultCode == RESULT_OK){
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);

                if (!credential.getId().isEmpty()) {
                    String[] aux = credential.getId().toString().split("55");
                    contato.setText(aux[1]);
                } else {

                }
            }else{
                escolherNumeroContato();
            }
        }
    }
//-------------------------------FIM Obter numero do chip e solicitar ao usuario para confirmar ----------

private void escolherNumeroContato(){
    dialog =new AlertDialog.Builder(this);
    dialog.setTitle("INFORMAÇÃO OBRIGATÓRIA");
    dialog.setMessage("Escolhe o seu número de Telefone para prosseguir.");

    //Para impedir que a dialog feche se clicar fora dele
    dialog.setCancelable(false);

    //Para inserir incone
    dialog.setIcon(android.R.drawable.ic_delete);

    dialog.setNegativeButton("Sair",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });

    dialog.setPositiveButton("OK",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

            obterNumeroAtual();



                }
            });


    dialog.create();
    dialog.show();
}



}
