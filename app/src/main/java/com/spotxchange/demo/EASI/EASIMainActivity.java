package com.spotxchange.demo.EASI;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.spotxchange.demo.R;

public class EASIMainActivity extends Activity {

    public static String EASI_DOCUMENT = "EASI_DOCUMENT";

    EditText _editTextEasiUrl;
    EditText _editTextEasiParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easimain);

        _editTextEasiUrl = (EditText)findViewById(R.id.edittext_easi_url);
        _editTextEasiParams = (EditText)findViewById(R.id.edittext_easi_params);

        findViewById(R.id.showAdInterstitial).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String easiTemplate = loadTemplate("easi_interstitial_template.html");
                showEASIDocument(easiTemplate);
            }
        });

        findViewById(R.id.showAdInContent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String easiTemplate = loadTemplate("easi_incontent_template.html");
                showEASIDocument(easiTemplate);
            }
        });
    }

    private void showEASIDocument(String templateText) {
        String easiDoc = String.format(
                templateText,
                _editTextEasiUrl.getText().toString(),
                _editTextEasiParams.getText().toString()
        );

        Intent intent = new Intent(this, EASIPlayerActivity.class);
        intent.putExtra(EASI_DOCUMENT, easiDoc);

        startActivity(intent);
    }

    private String loadTemplate(String name) {
        StringBuilder docText = new StringBuilder();
        try {
            InputStream inputStream = getAssets().open(name);
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String str;

            while ((str=in.readLine()) != null) {
                docText.append(str);
            }

            in.close();
        }
        catch (IOException ex) {

        }

        return docText.toString();
    }
}
