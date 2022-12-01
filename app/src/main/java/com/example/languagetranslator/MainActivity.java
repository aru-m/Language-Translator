package com.example.languagetranslator;

import static com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.HI;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.TranslateLanguage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;


import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
private Spinner fromSpinner,toSpinner;
private TextInputEditText sourceEdt;
private ImageView micIV;
private MaterialButton translateBtn;
private TextView translatedTV;
String[] fromLanguage={"From","Arabic","Chinese (Simplified)", "English", "French", "German","Gujarati","Hindi", "Indonesian","Italian","Japanese","Kannada","Korean","Latin","Marathi","Nepali","Portuguese","Romanian","Russian","Scottish","Sindhi","Spanish","Swedish","Tamil","Telugu","Thai","Turkish","Urdu","Vietnamese"};

String[] toLanguage={"To","Arabic","Chinese (Simplified)", "English", "French", "German","Gujarati","Hindi", "Indonesian","Italian","Japanese","Kannada","Korean","Latin","Marathi","Nepali", "Portuguese","Romanian","Russian","Scottish","Sindhi","Spanish","Swedish","Tamil","Telugu","Thai","Turkish","Urdu","Vietnamese"};

private static int REQUEST_PERMISSION_CODE=1;
private int languageCode,fromLanguageCode,toLanguageCode=0;
private Object FirebaseTranslateLanguage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        isme glti bta rha h iska mtlb iski xml me prb h
        fromSpinner=findViewById(R.id.idFromSpinner);
        toSpinner=findViewById(R.id.idToSpinner);
        sourceEdt=findViewById(R.id.idEdtSource);
        micIV=findViewById(R.id.idIVMic);
        translateBtn=findViewById(R.id.idBtnTranslate);
        translatedTV=findViewById(R.id.idTVTranslatedTV);

        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
fromLanguageCode=getLanguageCode(fromLanguage[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter fromAdapter=new ArrayAdapter(this,R.layout.spinner_item,fromLanguage);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdapter);

        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                toLanguageCode=getLanguageCode(toLanguage[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter toAdapter=new ArrayAdapter(this,R.layout.spinner_item,toLanguage);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);

        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translatedTV.setText("");
                if(sourceEdt.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter your text to translate", Toast.LENGTH_SHORT).show();
                }else if(fromLanguageCode==0){
                    Toast.makeText(MainActivity.this, "Please select source language", Toast.LENGTH_SHORT).show();
                }else if(toLanguageCode==0){
                    Toast.makeText(MainActivity.this, "Please select to language", Toast.LENGTH_SHORT).show();
                }else{
TranslateText(fromLanguageCode,toLanguageCode,sourceEdt.getText().toString());
                }
            }
        });

        micIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                i.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak to convert into text");
                try {
                    startActivityForResult(i,REQUEST_PERMISSION_CODE);
                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_PERMISSION_CODE){
            if(resultCode==RESULT_OK && data!=null){
                ArrayList<String> result= data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                sourceEdt.setText(result.get(0));
            }
        }
    }

    private void TranslateText(int fromLanguageCode, int toLanguageCode, String source){
translatedTV.setText("Downloading model....");
        FirebaseTranslatorOptions options=new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromLanguageCode)
                .setTargetLanguage(toLanguageCode)
                .build();
        FirebaseTranslator translator= FirebaseNaturalLanguage.getInstance().getTranslator(options);
        FirebaseModelDownloadConditions conditions=new FirebaseModelDownloadConditions.Builder().build();
translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
    @Override
    public void onSuccess(Void unused) {
        translatedTV.setText("Translating..");
        translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                translatedTV.setText(s);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Fail to translate"+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}).addOnFailureListener(new OnFailureListener() {
    @Override
    public void onFailure(@NonNull Exception e) {
        Toast.makeText(MainActivity.this, "Failed to download language model"+e.getMessage(), Toast.LENGTH_SHORT).show();
    }
});
    }


    public int getLanguageCode(String language){
        languageCode =0;
       switch (language){

           case "Arabic":
               languageCode= com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.AR;
               break;
           case "Chinese (Simplified)":
               languageCode= com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.ZH;
               break;
           //remove dutch from string array
           case"English":
               languageCode= com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.EN;
               break;
           case "French":
               languageCode= com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.FR;
               break;
           case "German":
               languageCode= com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.DE;
               break;
//           case "Greek":
//               languageCode= com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.GA;
//               break;
           case"Gujarati":
               languageCode= com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.GU;
               break;
           case "Hindi":
               languageCode= com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.HI;
               break;
           case "Indonesian":
               languageCode= com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.ID;
               break;
//           case  "Irish":
//               languageCode= com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.IR;
////               languageCode= FirebaseTranslateLanguage.IR;
//               break;
           case"Italian":
               languageCode= com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.IT;
               break;
           case "Japanese":
               languageCode= com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.JA;
               break;
           case "Kannada":
               languageCode= com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.KA;
               break;
           case "Korean":
               languageCode= com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.KN;
               break;
           case"Latin":
               languageCode= com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.LT;
               break;
//           case "Malayalam":
//               languageCode= com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.ML;
//               break;
           case "Marathi":
               languageCode= com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.MR;
               break;
           case"Nepali":
               languageCode= com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.NL;
               break;
//           case"Odia":
//               languageCode= com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.OR;
//               break;
//           case "Persian":
//               languageCode= com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.F;
//               break;
           case"Portuguese":
               languageCode= com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.PL;
               break;
//           case "Punjabi":
//               languageCode= com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.PB;
//               break;
           case "Romanian":
               languageCode= com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.RO;
               break;
           case "Russian":
               languageCode= com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.RU;
               break;
           case "Scottish":
               languageCode= com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage.SK;
               break;
           default:
               languageCode=0;

       }
       return languageCode;
    }
}