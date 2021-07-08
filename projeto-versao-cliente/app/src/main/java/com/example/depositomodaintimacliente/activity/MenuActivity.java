package com.example.depositomodaintimacliente.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.depositomodaintimacliente.R;
import com.example.depositomodaintimacliente.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class MenuActivity extends AppCompatActivity {
    private TextView contatosImportantes, politicaPrivacidade,mensagensAoUsuarios;
    private AlertDialog.Builder dialog;
    private DatabaseReference mensagemUsuarioRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        getSupportActionBar().setTitle("Menu");

        contatosImportantes = findViewById(R.id.idContatosImportantes);
        politicaPrivacidade = findViewById(R.id.idPoliticaPrivacidade);
        mensagensAoUsuarios = findViewById(R.id.idMensagemUsuario);

        mensagemUsuarioRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("mensagemAoUsuario").child("mensagem");


    }

    public void abrirContatosImportantes(View view){
        String aux;
        aux= "Chagas: (85) 9 9680-1608\n\n" +
                "Tatiane: (85) 9 9722-2897";

        chamarDialog("CONTATOS IMPORTANTES", aux,getResources().getDrawable(R.drawable.ic_info));

    }

    public void abrirMensagensAoUsuario(View view){
        mensagemUsuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String aux = dataSnapshot.getValue(String.class);
                chamarDialog("MENSAGEM DO DEPÓSITO",aux,getResources().getDrawable(R.drawable.ic_mensagem));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void abrirPoliticaPrivacidade(View view){
        String aux;
        aux= "Termos de Uso e Política de Privacidade\n" +
                "\n" +
                "Ao instalar e usar o aplicativo Depósito da Moda Íntima," +
                " o Usuário se submeterá automaticamente às regras e condições.\n" +
                "\n" +
                "O aplicativo Depósito da Moda Íntima poderá," +
                " sem prévio aviso, bloquear e cancelar o acesso ao aplicativo" +
                " quando verificar que o Usuário praticou algum ato ou mantenha" +
                " conduta que viole as leis e regulamentos federais," +
                " estaduais e/ou municipais, contrarie as regras destes" +
                " Termos de Uso, ou viole os princípios da moral e dos bons costumes.\n" +
                "\n" +
                "Toda e qualquer ação executada pelo Usuário" +
                " durante o uso do aplicativo Depósito da Moda Íntima" +
                " será de sua exclusiva e integral responsabilidade, devendo" +
                " isentar e indenizar o Desenvolvedor de quaisquer" +
                " reclamações, prejuízos, perdas e danos causados" +
                " ao Desenvolvedor, em decorrência de tais ações ou manifestações.\n" +
                "\n" +
                "Segurança:  \n" +
                "O aplicativo Depósito da Moda Íntima" +
                " toma muito cuidado na implementação e manutenção da segurança" +
                " das informações pessoais do utilizador. " +
                "Empregamos procedimentos padrão da indústria e políticas" +
                " para garantir a segurança de suas informações, " +
                "e impedir o uso não autorizado de tais informações. " +
                "Entre outras medidas, protegemos as informações" +
                " pessoais do usuário através do uso," +
                " no entanto, o aplicativo Depósito da Moda Íntima" +
                " não garante que o acesso não autorizado nunca vai ocorrer.\n" +
                "\n" +
                "Este aplicativo inclui anúncios:\n" +
                "Fornecemos aos nossos parceiros algumas" +
                " informações sobre a forma como utiliza" +
                " o nosso aplicativo para que eles possam" +
                " anunciar produtos e serviços" +
                " com maior probabilidade de serem relevantes para você.\n" +
                "As redes de anúncios são empresas que" +
                " gerenciam o processo de publicidade neste" +
                " e em muitos outros aplicativos." +
                " Por esse motivo, eles podem saber informações" +
                " sobre você com base na maneira como você usa todos" +
                " os aplicativos onde eles fornecem serviços de publicidade.\n" +
                "\n" +
                "Você pode descobrir mais sobre como eles usam suas informações pessoais aqui:\n" +
                "Google AdMob (http://www.google.com.br/intl/pt-br/policies/privacy/).\n" +
                "\n" +
                "Permissões Sensíveis:\n" +
                "Para utilização de algumas funções deste" +
                " aplicativo é necessário a autorização de " +
                "uso de algumas permissões sensíveis do seu dispositivo, " +
                "estas permissões podem ser revogadas a qualquer momento.\n" +
                "\n" +
                "Gratuidade:\n" +
                "Todos os serviços e funcionalidades oferecidos pelo aplicativo" +
                " Depósito da Moda Íntima são inteiramente gratuitos," +
                " não sendo necessária a realização" +
                " de qualquer pagamento por parte dos nossos usuários.\n" +
                "\n" +
                "O APLICATIVO E SEU DESENVOLVEDOR" +
                " SE EXIME DE TODA E QUALQUER RESPONSABILIDADE" +
                " PELOS DANOS E PREJUÍZOS DE QUALQUER NATUREZA QUE" +
                " POSSAM DECORRER DO ACESSO, INTERCEPTAÇÃO," +
                " ELIMINAÇÃO, ALTERAÇÃO, MODIFICAÇÃO OU MANIPULAÇÃO," +
                " POR TERCEIROS NÃO AUTORIZADOS, DOS DADOS " +
                "DO USUÁRIO DURANTE A UTILIZAÇÃO DO APLICATIVO Depósito da Moda Íntima.";

        chamarDialog("POLÍTICA E PRIVACIDADE", aux,getResources().getDrawable(R.drawable.ic_security));

    }

    public void abrirInformacoesPagamento(View view){
        String aux = "Para realizar o pagamento do Seu Pedido " +
                "entre em contato com o WhatsAPP" +
                "(85) 9 9722-2897, para solicitar os " +
                "dados bancários para depósito/transferência.\n\n" +
                "Após o pagamento, enviar o comprovante " +
                "para o mesmo WhatsAPP e aguarde a " +
                "confirmação no STATUS de seu pedido.\n\n" +
                "Você pode acompanhar seu pedido\nclicando em Minhas Compras.\n\n" +
                "Agradecemos a preferência.\nDeus abençoe!";
        chamarDialog("INFORMAÇÕES SOBRE PAGAMENTO", aux, getResources().getDrawable(R.drawable.ic_deposito));
    }

    public void chamarDialog(String titulo, String mensagem, Drawable drawable){
        dialog =new AlertDialog.Builder(this);
        dialog.setTitle(titulo);
        dialog.setMessage(mensagem);

        //Para impedir que a dialog feche se clicar fora dele
        dialog.setCancelable(false);

        //Para inserir incone
        dialog.setIcon(drawable);

        dialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });


        dialog.create();
        dialog.show();
    }
}
