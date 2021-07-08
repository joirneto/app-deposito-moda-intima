package com.example.depositomodaintima.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.depositomodaintima.R;
import com.example.depositomodaintima.helper.ConfiguracaoFirebase;
import com.example.depositomodaintima.helper.Permissoes;
import com.example.depositomodaintima.model.Produto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class NovoProdutoActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText tituloProdutoNovo, precoProdutoNovo, descricaoProdutoNovo, ganhoProduto,
            quantidadeP, quantidadeM, quantidadeG, quantidadeGG, quantidadeU;
    private TextView textoPreco, textoGanho;
    private Button botaoProdutoNovo;
    private Spinner campoCategoria;
    private ImageView imagemProdutoNovo1, imagemProdutoNovo2, imagemProdutoNovo3, imagemProdutoNovo4,
            imagemProdutoNovo5, imagemProdutoNovo6;

    private String[] permissoes = new String[]{
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE

    };

    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaFotosURL = new ArrayList<>();

    private StorageReference storage;

    private Produto produto;

    private android.app.AlertDialog alertDialog;
    //ONCREATE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto);

        //Inicializando os atributos
        initAtributesNovoProduto();

        //Validar Permissoes
        Permissoes.validarPermissoes(permissoes, this,1);

        //Carregando opções Spinner
        carregarDadosSpinner();

        storage = ConfiguracaoFirebase.getFirebaseStorage();

        produto = new Produto();

        botaoProdutoNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validarDadosNovoProduto(produto)){
                    salvarNovoProduto(produto);


                }

            }
        });
    }

    //Sobreescrevendo o método OnCLick
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imagemNovoProduto1:
                escolherImagem(1);
                break;
            case R.id.imagemNovoProduto2:
                escolherImagem(2);
                break;
            case R.id.imagemNovoProduto3:
                escolherImagem(3);
                break;
            case R.id.imagemNovoProduto4:
                escolherImagem(4);
                break;
            case R.id.imagemNovoProduto5:
                escolherImagem(5);
                break;
            case R.id.imagemNovoProduto6:
                escolherImagem(6);
                break;

        }
    }

    //Método para verificar resultado do clique da imagem
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            //REcupera imagem
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            //Configura imagem no ImageView
            if (requestCode == 1) {
                imagemProdutoNovo1.setImageURI(imagemSelecionada);
            }else if (requestCode == 2) {
                imagemProdutoNovo2.setImageURI(imagemSelecionada);
            }else if (requestCode == 3) {
                imagemProdutoNovo3.setImageURI(imagemSelecionada);
            }else if (requestCode == 4) {
                imagemProdutoNovo4.setImageURI(imagemSelecionada);
            }else if (requestCode == 5) {
                imagemProdutoNovo5.setImageURI(imagemSelecionada);
            }else if (requestCode == 6) {
                imagemProdutoNovo6.setImageURI(imagemSelecionada);
            }
            listaFotosRecuperadas.add(caminhoImagem);
        }
    }//fim onActivityResult

    //Método para inicializar os atributos
    public void initAtributesNovoProduto(){
        tituloProdutoNovo = findViewById(R.id.idTituloProdutoNovo);
        precoProdutoNovo = findViewById(R.id.idPrecoProduto);
        descricaoProdutoNovo = findViewById(R.id.idDescricaoProdutoNovo);
        botaoProdutoNovo = findViewById(R.id.idBotaoProdutoNovo);
        campoCategoria = findViewById(R.id.idSpinnerProdutoNovo);
        imagemProdutoNovo1 = findViewById(R.id.imagemNovoProduto1);
        imagemProdutoNovo2 = findViewById(R.id.imagemNovoProduto2);
        imagemProdutoNovo3 = findViewById(R.id.imagemNovoProduto3);
        imagemProdutoNovo4 = findViewById(R.id.imagemNovoProduto4);
        imagemProdutoNovo5 = findViewById(R.id.imagemNovoProduto5);
        imagemProdutoNovo6 = findViewById(R.id.imagemNovoProduto6);
        imagemProdutoNovo1.setOnClickListener(this);
        imagemProdutoNovo2.setOnClickListener(this);
        imagemProdutoNovo3.setOnClickListener(this);
        imagemProdutoNovo4.setOnClickListener(this);
        imagemProdutoNovo5.setOnClickListener(this);
        imagemProdutoNovo6.setOnClickListener(this);

        ganhoProduto = findViewById(R.id.idGanho);
        textoGanho = findViewById(R.id.idTextoGanho);
        textoPreco = findViewById(R.id.idTextoProduto);

        quantidadeP = findViewById(R.id.idquantidadeP);
        quantidadeM = findViewById(R.id.idquantidadeM);
        quantidadeG = findViewById(R.id.idquantidadeG);
        quantidadeGG = findViewById(R.id.idquantidadeGG);
        quantidadeU = findViewById(R.id.idquantidadeU);

    }//fim  initAtributesNovoProduto

    //Método para carregar os Dados da Spinner

    public void carregarDadosSpinner(){
        String[] categorias = new String[]{
                "CATEGORIA", "Babydool", "Calcinha Adulto", "Calcinha Infantil",
                "Camisola", "Conjuntos", "Cueca Adulto", "Cueca Infantil",
                "Meia Masculina","Short","Sutiã", "Top Adulto", "Top Infantil"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categorias
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        campoCategoria.setAdapter(adapter);
    }//carregarDadosSpinner

    // Método para validar as Permissões
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int permissaoREsultado: grantResults){
            if(permissaoREsultado== PackageManager.PERMISSION_DENIED){
                alertaVAlidacaoPermissao();

            }
        }
    }//fim onRequestPermissionsResult

    //Método para criar Alerta das Permissoes
    private void alertaVAlidacaoPermissao(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões!");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }//fim alertaVAlidacaoPermissao()

    //Método para Escolher a imagem
    public void escolherImagem(int requestCode){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i,requestCode);
    }//fim escolherImagem()


    //Método para validar os dados digitados para cadastrar novo produto
    public boolean validarDadosNovoProduto(Produto produto){
        produto.setCategoria(campoCategoria.getSelectedItem().toString());
        produto.setTitulo(tituloProdutoNovo.getText().toString());
        produto.setPreco(precoProdutoNovo.getText().toString());
        preencherQtdTamanhos();
        produto.setDescricao(descricaoProdutoNovo.getText().toString());
        produto.setListaFotos(listaFotosRecuperadas);
        produto.setGanho(ganhoProduto.getText().toString());
        produto.setQtdVendidos("0");

        if(listaFotosRecuperadas.size()!=0){
            if(!produto.getCategoria().equals("CATEGORIA")){
                if(!produto.getTitulo().isEmpty()) {
                    if(!produto.getGanho().equals("R$0,00")){
                        if (!produto.getPreco().equals("R$0,00")) {
                            if(!quantidadeP.getText().toString().isEmpty()&&
                                    !quantidadeM.getText().toString().isEmpty()&&
                                    !quantidadeG.getText().toString().isEmpty()&&
                                    !quantidadeGG.getText().toString().isEmpty()&&
                                    !quantidadeU.getText().toString().isEmpty()) {


                                if (!produto.getDescricao().isEmpty()) {
                                    return true;
                                }//fim de descrição
                                else {
                                    Toast.makeText(this,
                                            "Preencha a descrição do produto!",
                                            Toast.LENGTH_SHORT).show();
                                }//fim de descrição
                            }
                            else{
                                Toast.makeText(this,
                                        "Informe as quantidades por tamanhos dos produto!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }//aq
                        else {
                            Toast.makeText(this,
                                    "Favor inserir o PREÇO do produto!",
                                    Toast.LENGTH_SHORT).show();

                        }

                    }else{

                        Toast.makeText(this,
                                "Informe o GANHO para o produto!",
                                Toast.LENGTH_SHORT).show();

                    }


                }       else{
                    Toast.makeText(this,
                            "Informe o TÍTULO para o produto!",
                            Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(this,
                        "Selecione a CATEGORIA para o produto!",
                        Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this,
                    "Selecione pelo menos UMA FOTO!",
                    Toast.LENGTH_SHORT).show();
        }

        return false;
    }// fim alidarDadosNovoProduto

    //Método para salvar dados do Novo produto
    public void salvarNovoProduto(Produto produto){

        //Tela de alerta enquanto o sistema salva o produto
        alertDialog = new SpotsDialog.Builder().
                setContext(this).
                setMessage("Salvando Produto...").
                setCancelable(false).build();
        alertDialog.show();


        for(int i=0;i<listaFotosRecuperadas.size();i++){
            String urlImagem = listaFotosRecuperadas.get(i);
            salvarFotoStorage(urlImagem,listaFotosRecuperadas.size(),i);
        }

    }//salvarNovoProduto()

    //Método para salvar a foto
    private void salvarFotoStorage(final String urlImagem, final int totalFotos, int contador){
        //Cria nó do Storage
        StorageReference imagemProduto =storage.child("imagens").
                child("produtos").
                child(produto.getIdProduto()).
                child("imagem"+contador);

        //Fazer upLoad da imagem
        UploadTask uploadTask = imagemProduto.putFile(Uri.parse(urlImagem));

        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> firebaseUrl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Log.i("Lista00:", String.valueOf(task.getResult()));
                        String urlConvertida= task.getResult().toString();
                        listaFotosURL.add(urlConvertida);

                        if(totalFotos==listaFotosURL.size()){
                            produto.setListaFotos(listaFotosURL);
                            Log.i("Lista1:",listaFotosURL.get(0));
                            produto.salvar();
                            alertDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(), Main2Activity.class));
                            finish();

                        }

                    }
                });




            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NovoProdutoActivity.this,
                        "Erro ao fazer o UPLOAD da imagem!",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();;
    }

    public void preencherQtdTamanhos(){
        HashMap<String,String> aux = new HashMap<>();

        if(quantidadeP.getText().toString().isEmpty()){
            quantidadeP.setText("0");
        }

        if(quantidadeM.getText().toString().isEmpty()){
            quantidadeM.setText("0");
        }

        if(quantidadeG.getText().toString().isEmpty()){
            quantidadeG.setText("0");
        }

        if(quantidadeGG.getText().toString().isEmpty()){
            quantidadeGG.setText("0");
        }

        if(quantidadeU.getText().toString().isEmpty()){
            quantidadeU.setText("0");
        }

        converterZeroNovo();
        aux.put("P",quantidadeP.getText().toString());
        aux.put("M",quantidadeM.getText().toString());
        aux.put("G",quantidadeG.getText().toString());
        aux.put("GG",quantidadeGG.getText().toString());
        aux.put("U",quantidadeU.getText().toString());

        produto.setTamanhosEquantidades(aux);

    }

    public void converterZeroNovo(){

        if(quantidadeP.getText().toString().equals("00")||
                quantidadeP.getText().toString().equals("000")||
                quantidadeP.getText().toString().equals("0000")||
                quantidadeP.getText().toString().equals("00000")){
            quantidadeP.setText("0");

        }

        if(quantidadeM.getText().toString().equals("00")||
                quantidadeM.getText().toString().equals("000")||
                quantidadeM.getText().toString().equals("0000")||
                quantidadeM.getText().toString().equals("00000")){
            quantidadeM.setText("0");

        }

        if(quantidadeG.getText().toString().equals("00")||
                quantidadeG.getText().toString().equals("000")||
                quantidadeG.getText().toString().equals("0000")||
                quantidadeG.getText().toString().equals("00000")){
            quantidadeG.setText("0");

        }

        if(quantidadeGG.getText().toString().equals("00")||
                quantidadeGG.getText().toString().equals("000")||
                quantidadeGG.getText().toString().equals("0000")||
                quantidadeGG.getText().toString().equals("00000")){
            quantidadeGG.setText("0");

        }

        if(quantidadeU.getText().toString().equals("00")||
                quantidadeU.getText().toString().equals("000")||
                quantidadeU.getText().toString().equals("0000")||
                quantidadeU.getText().toString().equals("00000")){
            quantidadeU.setText("0");

        }
    }
}
