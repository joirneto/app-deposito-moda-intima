package com.example.depositomodaintima.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.Toast;

import com.example.depositomodaintima.R;
import com.example.depositomodaintima.helper.ConfiguracaoFirebase;
import com.example.depositomodaintima.helper.Permissoes;
import com.example.depositomodaintima.model.Produto;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class EditarProdutoActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText tituloProdutoEditar, precoProdutoEditar, descricaoProdutoEditar, ganhoProdutoEditar;
    private Button botaoProdutoEditar;
    private Spinner campoCategoriaEditar;
    private ImageView imagemProdutoEditar1, imagemProdutoEditar2, imagemProdutoEditar3, imagemProdutoEditar4,
            imagemProdutoEditar5, imagemProdutoEditar6;

    private EditText quantidadeP, quantidadeM, quantidadeG, quantidadeGG, quantidadeU;

    private HashMap<String,String> listaFotosRecuperadas = new HashMap<>();
    private HashMap<String,String> listaFotosRecuperadasAux = new HashMap<>();
    private HashMap<String,String> listaFotosURLaux = new HashMap<>();
    private List<String> listaFotosURL = new ArrayList<>();

    private DatabaseReference produtoRef;

    private StorageReference storage;
    private StorageReference storageExcluir;

    private Produto produto;

    private android.app.AlertDialog alertDialog;
    private AlertDialog.Builder alertExcluir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_produto);

        //Inicializando os atributos
        initAtributesEditarProduto();

        //Carregando opções Spinner
        carregarDadosSpinner();

        produto = (Produto) getIntent().getSerializableExtra("produtoSelecionado");

        recuperarDadosProduto(produto);

        storage = ConfiguracaoFirebase.getFirebaseStorage();

        botaoProdutoEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validarDadosEditarProduto(produto)){
                    salvarEditarProduto(produto);
                }
            }
        });
    }

    //Sobreescrevendo o método OnCLick
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imagemEditarProduto1:
                escolherImagem(1);
                break;
            case R.id.imagemEditarProduto2:
                escolherImagem(2);
                break;
            case R.id.imagemEditarProduto3:
                escolherImagem(3);
                break;
            case R.id.imagemEditarProduto4:
                escolherImagem(4);
                break;
            case R.id.imagemEditarProduto5:
                escolherImagem(5);
                break;
            case R.id.imagemEditarProduto6:
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

            int aux = listaFotosRecuperadas.size() + listaFotosURLaux.size();

            //Configura imagem no ImageView
            if (requestCode == 1) {
                imagemProdutoEditar1.setImageURI(imagemSelecionada);
                if(listaFotosURLaux.get("0")!=null){
                    listaFotosURLaux.remove("0");
                    listaFotosRecuperadas.put(String.valueOf("0"),caminhoImagem);
                }
                else{
                    listaFotosRecuperadas.put(String.valueOf("0"),caminhoImagem);
                }
            }else if (requestCode == 2) {
                imagemProdutoEditar2.setImageURI(imagemSelecionada);
                if(listaFotosURLaux.get("1")!=null){
                    listaFotosURLaux.remove("1");
                    listaFotosRecuperadas.put(String.valueOf("1"),caminhoImagem);
                }
                else{
                    listaFotosRecuperadas.put(String.valueOf("1"),caminhoImagem);
                }
            }else if (requestCode == 3) {
                imagemProdutoEditar3.setImageURI(imagemSelecionada);
                if(listaFotosURLaux.get("2")!=null){
                    listaFotosURLaux.remove("2");
                    listaFotosRecuperadas.put(String.valueOf("2"),caminhoImagem);
                }
                else{
                    listaFotosRecuperadas.put(String.valueOf("2"),caminhoImagem);
                }
            }else if (requestCode == 4) {
                imagemProdutoEditar4.setImageURI(imagemSelecionada);
                if(listaFotosURLaux.get("3")!=null){
                    listaFotosURLaux.remove("3");
                    listaFotosRecuperadas.put(String.valueOf("3"),caminhoImagem);
                }
                else{
                    listaFotosRecuperadas.put(String.valueOf("3"),caminhoImagem);
                }
            }else if (requestCode == 5) {
                imagemProdutoEditar5.setImageURI(imagemSelecionada);
                if(listaFotosURLaux.get("4")!=null){
                    listaFotosURLaux.remove("4");
                    listaFotosRecuperadas.put(String.valueOf("4"),caminhoImagem);
                }
                else{
                    listaFotosRecuperadas.put(String.valueOf("4"),caminhoImagem);
                }
            }else if (requestCode == 6) {
                imagemProdutoEditar6.setImageURI(imagemSelecionada);
                Log.i("Foto4: ", String.valueOf(listaFotosURL.size()));
                if(listaFotosURLaux.get("5")!=null){
                    listaFotosURLaux.remove("5");
                    listaFotosRecuperadas.put(String.valueOf("5"),caminhoImagem);
                }
                else{
                    listaFotosRecuperadas.put(String.valueOf("5"),caminhoImagem);
                }
            }

        }
    }//fim onActivityResult

    //Método para inicializar os atributos
    public void initAtributesEditarProduto(){
        tituloProdutoEditar = findViewById(R.id.idTituloProdutoEditar);
        precoProdutoEditar = findViewById(R.id.idPrecoProdutoEditar);
        descricaoProdutoEditar = findViewById(R.id.idDescricaoProdutoEditar);
        botaoProdutoEditar = findViewById(R.id.idBotaoProdutoEditar);
        ganhoProdutoEditar = findViewById(R.id.idGanhoEditar);
        campoCategoriaEditar = findViewById(R.id.idSpinnerProdutoEditar);
        imagemProdutoEditar1 = findViewById(R.id.imagemEditarProduto1);
        imagemProdutoEditar2 = findViewById(R.id.imagemEditarProduto2);
        imagemProdutoEditar3 = findViewById(R.id.imagemEditarProduto3);
        imagemProdutoEditar4 = findViewById(R.id.imagemEditarProduto4);
        imagemProdutoEditar5 = findViewById(R.id.imagemEditarProduto5);
        imagemProdutoEditar6 = findViewById(R.id.imagemEditarProduto6);
        imagemProdutoEditar1.setOnClickListener(this);
        imagemProdutoEditar2.setOnClickListener(this);
        imagemProdutoEditar3.setOnClickListener(this);
        imagemProdutoEditar4.setOnClickListener(this);
        imagemProdutoEditar5.setOnClickListener(this);
        imagemProdutoEditar6.setOnClickListener(this);

        quantidadeP = findViewById(R.id.idquantidadeEditarP);
        quantidadeM = findViewById(R.id.idquantidadeEditarM);
        quantidadeG = findViewById(R.id.idquantidadeEditarG);
        quantidadeGG = findViewById(R.id.idquantidadeEditarGG);
        quantidadeU = findViewById(R.id.idquantidadeEditarU);

    }//fim  initAtributesEditarProduto

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
        campoCategoriaEditar.setAdapter(adapter);
    }//carregarDadosSpinner

    //Método para Escolher a imagem
    public void escolherImagem(int requestCode){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i,requestCode);
    }//fim escolherImagem()


    //Método para validar os dados digitados para cadastrar Editar produto
    public boolean validarDadosEditarProduto(Produto produto){
        produto.setCategoria(campoCategoriaEditar.getSelectedItem().toString());
        produto.setTitulo(tituloProdutoEditar.getText().toString());
        produto.setPreco(precoProdutoEditar.getText().toString());
        produto.setDescricao(descricaoProdutoEditar.getText().toString());
        produto.setGanho(ganhoProdutoEditar.getText().toString());
        HashMap<String,String> hashMapAux = new HashMap<>();

        converterZero();

        hashMapAux.put("P", quantidadeP.getText().toString());
        hashMapAux.put("M", quantidadeM.getText().toString());
        hashMapAux.put("G", quantidadeG.getText().toString());
        hashMapAux.put("GG", quantidadeGG.getText().toString());
        hashMapAux.put("U",quantidadeU.getText().toString());
        produto.setTamanhosEquantidades(hashMapAux);



        if(listaFotosRecuperadas.size()+listaFotosURLaux.size()!=0){
            if(!produto.getCategoria().equals("CATEGORIA")){
                if(!produto.getTitulo().isEmpty()) {
                    if (!produto.getPreco().equals("R$0,00")) {
                        if(!produto.getGanho().equals("R$0,00")&&!produto.getGanho().isEmpty()){
                            if(!quantidadeP.getText().toString().isEmpty()&&
                                !quantidadeM.getText().toString().isEmpty()&&
                                !quantidadeG.getText().toString().isEmpty()&&
                                !quantidadeGG.getText().toString().isEmpty()&&
                                !quantidadeU.getText().toString().isEmpty()){


                                if (!produto.getDescricao().isEmpty()) {
                                    return true;
                                }//fim de descrição
                                else {
                                    Toast.makeText(this,
                                            "Preencha a descrição do produto!",
                                            Toast.LENGTH_SHORT).show();
                                }//fim de descrição
                            }else {
                                Toast.makeText(this,
                                        "Informe as quantidades por tamanhos dos produto!",
                                        Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(this,
                                    "Favor inserir o GANHO do produto!",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }//fim de quantidade
                    else {
                        Toast.makeText(this,
                                "Favor inserir o PREÇO do produto!",
                                Toast.LENGTH_SHORT).show();

                    }//fim de quantidade
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
    }// fim alidarDadosEditarProduto

    //Método para salvar dados do Editar produto
    public void salvarEditarProduto(Produto produto){

        //Tela de alerta enquanto o sistema salva o produto
        alertDialog = new SpotsDialog.Builder().
                setContext(this).
                setMessage("Salvando Produto Editado...").
                setCancelable(false).build();
        alertDialog.show();

        if(listaFotosURLaux.size()!=0){
            for (Map.Entry<String,String> entry : listaFotosURLaux.entrySet()) {
                listaFotosURL.add(entry.getValue());
            }
        }

        if(listaFotosRecuperadas.size()!=0){
            for (Map.Entry<String,String> entry : listaFotosRecuperadas.entrySet()) {
                int contador =  Integer.parseInt(entry.getKey());
                String urlImagem = entry.getValue();
                salvarFotoStorage(urlImagem,listaFotosRecuperadas.size()+listaFotosURLaux.size(),contador);

            }

        }else{
            produto.salvar();
            alertDialog.dismiss();
            startActivity(new Intent(getApplicationContext(), Main2Activity.class));
            finish();

        }


    }//salvarEditarProduto()

    //Método para salvar a foto
    private void salvarFotoStorage(final String urlImagem, final int totalFotos, final int contador){
        //Cria nó do Storage
        StorageReference imagemProduto =storage.child("imagens").
                child("produtos").
                child(produto.getIdProduto()).
                child("imagem"+contador);
        UploadTask uploadTask = imagemProduto.putFile(Uri.parse(urlImagem));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> firebaseUrl = taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        String urlConvertida= task.getResult().toString();
                        listaFotosURL.add(urlConvertida);
                        listaFotosRecuperadasAux.put(String.valueOf(contador),urlConvertida);

                        if(totalFotos==listaFotosURL.size()){
                            sortHash(listaFotosURLaux,listaFotosRecuperadasAux);
                            produto.setListaFotos(listaFotosURL);
                            Log.i("UUU2: ", String.valueOf(listaFotosURL));
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
                Toast.makeText(EditarProdutoActivity.this,
                        "Erro ao fazer o UPLOAD da imagem!",
                        Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();;
    }

    public void recuperarDadosProduto(Produto produto){
        tituloProdutoEditar.setText(produto.getTitulo());
        precoProdutoEditar.setText(produto.getPreco());
        descricaoProdutoEditar.setText(produto.getDescricao());
        quantidadeP.setText(produto.getTamanhosEquantidades().get("P"));
        quantidadeM.setText(produto.getTamanhosEquantidades().get("M"));
        quantidadeG.setText(produto.getTamanhosEquantidades().get("G"));
        quantidadeGG.setText(produto.getTamanhosEquantidades().get("GG"));
        quantidadeU.setText(produto.getTamanhosEquantidades().get("U"));
        ganhoProdutoEditar.setText(produto.getGanho());

        switch (produto.getCategoria()){
            case "Babydool":
                campoCategoriaEditar.setSelection(1);
                break;
            case "Calcinha Adulto":
                campoCategoriaEditar.setSelection(2);
                break;
            case "Calcinha Infantil":
                campoCategoriaEditar.setSelection(3);
                break;
            case "Camisola":
                campoCategoriaEditar.setSelection(4);
                break;
            case "Conjuntos":
                campoCategoriaEditar.setSelection(5);
                break;
            case "Cueca Adulto":
                campoCategoriaEditar.setSelection(6);
                break;
            case "Cueca Infantil":
                campoCategoriaEditar.setSelection(7);
                break;
            case "Meia Masculina":
                campoCategoriaEditar.setSelection(8);
                break;
            case "Short":
                campoCategoriaEditar.setSelection(9);
                break;
            case "Sutiã":
                campoCategoriaEditar.setSelection(10);
                break;
            case "Top Adulto":
                campoCategoriaEditar.setSelection(11);
                break;
            case "Top Infantil":
                campoCategoriaEditar.setSelection(12);
                break;
        }

        recuperarFotos(produto);
    }
    public void recuperarFotos(Produto produto){

        List<String> fotos = produto.getListaFotos();

        for(int i = 0;i<fotos.size();i++){
            String urlCapa = fotos.get(i);
            if(i==0){
                Picasso.get().load(urlCapa).resize(380, 380).centerInside().into(imagemProdutoEditar1);
            }
            else if(i==1){
                Picasso.get().load(urlCapa).resize(380, 380).centerInside().into(imagemProdutoEditar2);
            }
            else if(i==2){
                Picasso.get().load(urlCapa).resize(380, 380).centerInside().into(imagemProdutoEditar3);
            }
            else if(i==3){
                Picasso.get().load(urlCapa).resize(380, 380).centerInside().into(imagemProdutoEditar4);
            }
            else if(i==4){
                Picasso.get().load(urlCapa).resize(380, 380).centerInside().into(imagemProdutoEditar5);
            }
            else if(i==5){
                Picasso.get().load(urlCapa).resize(380, 380).centerInside().into(imagemProdutoEditar6);
            }
            listaFotosURLaux.put(String.valueOf(i),urlCapa);



        }
    }

    public void sortHash(HashMap<String,String> mapa1,HashMap<String,String> mapa2){
        listaFotosURL.clear();
        for(int i=0;i<6;i++){
            String aux = String.valueOf(i);
            if (mapa1.get(aux)!=null){
                listaFotosURL.add(mapa1.get(aux));
            }
            else if(mapa2.get(aux)!=null) {
                listaFotosURL.add(mapa2.get(aux));
            }
        }
    }

    public void excluirProduto(View view){
        alertExcluir =new AlertDialog.Builder(this);
        alertExcluir.setTitle("EXCLUIR PRODUTO");
        alertExcluir.setMessage("Tem certeza que deseja EXCLUIR o protudo do estoque?");

        //Para impedir que a dialog feche se clicar fora dele
        alertExcluir.setCancelable(false);

        //Para inserir incone
        alertExcluir.setIcon(android.R.drawable.ic_delete);

        alertExcluir.setNegativeButton("Não",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        alertExcluir.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        produtoRef = ConfiguracaoFirebase.getFirebaseDatabase().child("produtos");
                        produtoRef.child(produto.getIdProduto()).removeValue();

                        produtoRef = ConfiguracaoFirebase.getFirebaseDatabase()
                                .child("produtosCategoria")
                                .child(produto.getCategoria());
                        produtoRef.child(produto.getIdProduto()).removeValue();

                        for(int i=0;i<produto.getListaFotos().size();i++){
                            storageExcluir = ConfiguracaoFirebase.getFirebaseStorage()
                                    .child("imagens")
                                    .child("produtos")
                                    .child(produto.getIdProduto()+"/");

                            storageExcluir.child("imagem"+String.valueOf(i)).delete();
                        }

                        Toast.makeText(EditarProdutoActivity.this,
                                "Produto Excluído com SUCESSO!",
                                Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(getApplicationContext(), Main2Activity.class));
                        finish();


                    }
                });


        alertExcluir.create();
        alertExcluir.show();

    }

    public void limparFotos(View view){
        listaFotosURL.clear();
        listaFotosURLaux.clear();
        listaFotosRecuperadas.clear();
        listaFotosRecuperadasAux.clear();

        imagemProdutoEditar1.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.padrao));
        imagemProdutoEditar2.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.padrao));
        imagemProdutoEditar3.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.padrao));
        imagemProdutoEditar4.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.padrao));
        imagemProdutoEditar5.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.padrao));
        imagemProdutoEditar6.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.padrao));
    }

    public void converterZero(){

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
