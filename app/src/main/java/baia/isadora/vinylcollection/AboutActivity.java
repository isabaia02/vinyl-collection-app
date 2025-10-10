package baia.isadora.vinylcollection;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_about);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setTitle(R.string.about_button);
    }

    public void openGithub(View view){
        openSite("https://github.com/isabaia02");
    }

    private void openSite(String address){
        Intent intentOpening = new Intent(Intent.ACTION_VIEW);
        intentOpening.setData(Uri.parse(address));

        if (intentOpening.resolveActivity(getPackageManager()) != null){
            startActivity(intentOpening);
        }else{
            Toast.makeText(this, R.string.nenhum_aplicativo_para_abrir_p_ginas_web, Toast.LENGTH_LONG).show();
        }
    }

    public void sendEmailAuthor(View view){
        sendEmail(new String[]{"ibaia@alunos.utfpr.edu.br"}, "Contato pelo aplicativo");
    }

    private void sendEmail (String[] addresses, String subject){
        Intent intentOpening = new Intent(Intent.ACTION_SENDTO);

        intentOpening.setData(Uri.parse("mailto:"));
        intentOpening.putExtra(Intent.EXTRA_EMAIL, addresses);
        intentOpening.putExtra(Intent.EXTRA_SUBJECT, subject);

        if (intentOpening.resolveActivity(getPackageManager()) != null){
            startActivity(intentOpening);
        }else{
            Toast.makeText(this, R.string.nenhum_aplicativo_para_enviar_o_email, Toast.LENGTH_LONG).show();
        }
    }
}