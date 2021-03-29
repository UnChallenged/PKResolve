package com.myapp.Codex.pkresolve;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    public Button btn;
    public TextView txtNS, txtCDATE, txtEDATE, whatdomain;
    public StringBuilder response;
    public String text = null, rspnsCode;
    public Pattern pattern, pattern2, pattern3;
    public Matcher matcher, matcher2, matcher3;
    public EditText domain;
    public String extDomain = null, domainname = null;
    public ProgressBar pbar;
    private AdView adview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, getString(R.string.admob_app_id));

        adview = (AdView) findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        adview.loadAd(adRequest);
   //     adviewlandscape.loadAd(adRequest);

        btn = (Button) findViewById(R.id.button);
        txtNS = (TextView) findViewById(R.id.textView2);
        txtCDATE = (TextView) findViewById(R.id.textView6);
        txtEDATE = (TextView) findViewById(R.id.textView7);
        whatdomain = (TextView) findViewById(R.id.textView);



        txtNS.setMovementMethod(new ScrollingMovementMethod());

        domain = (EditText) findViewById(R.id.editText2);
        pbar = (ProgressBar) findViewById(R.id.progressBar);

        //get the spinner from the xml.
        Spinner dropdown = findViewById(R.id.spinner1);
//create a list of items for the spinner.
        String[] items = new String[]{".pk", ".com.pk", ".edu.pk", ".net.pk", ".org.pk"};
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        extDomain = ".pk";
                        break;
                    case 1:
                        extDomain = ".com.pk";
                        break;
                    case 2:
                        extDomain = ".edu.pk";
                        break;
                    case 3:
                        extDomain = ".net.pk";
                        break;
                    case 4:
                        extDomain = ".org.pk";
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        domain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                domain.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                domain.setError(null);
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                domainname = domain.getText().toString();

                if (domain.getText().toString().trim().equalsIgnoreCase("")) {
                    domain.requestFocus();
                    domain.setError("This field can not be blank");
                } else {
                    txtCDATE.setText("");
                    txtEDATE.setText("");
                    txtNS.setText("");

                    postData();
                    whatdomain.setText("");
                    pbar.setVisibility(View.VISIBLE);
                }


            }
        });

    }

    public void postData() {
// Create a new HttpClient and Post Header
        new PostData().execute();
    }

    //   String server_response;
    // HttpURLConnection urlConnection = null;

    public class PostData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
/*
            URL url;
            try {
                url = new URL("http://www.pakwhois.com");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                int responseCode = urlConnection.getResponseCode();
                rspnsCode = String.valueOf(responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.i("CatalogClient", rspnsCode);*/


            try {
                URLConnection connection = new URL("http://www.pakwhois.com/php/viewWhois.php?Name=" + domainname + "&Domain=" + extDomain).openConnection();

                connection.setRequestProperty("Accept-Charset", "UTF-8");
                InputStream responseStream = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(responseStream));
                response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                text = response.toString();
                Log.i("Output", text);
                //    Log.i("URL IS : ", String.valueOf(connection));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

/*            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/


            pattern = Pattern.compile("<h2>Nameservers:</h2>(.*?)<br>Domain Lookup");
            pattern2 = Pattern.compile(">Create Date: </b>(.*?)<br><b>Expire Date: ");
            pattern3 = Pattern.compile("Expire Date: </b>(.*?)<br><h2>Nameservers:");


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            // Toast.makeText(getBaseContext(), "Sent", Toast.LENGTH_SHORT).show();

            if (text != null) {

                matcher = pattern.matcher(text);
                matcher2 = pattern2.matcher(text);
                matcher3 = pattern3.matcher(text);
                if (matcher.find()) {
                    Log.i("Output for button", matcher.group(1));
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        txtNS.setText(Html.fromHtml(matcher.group(1), Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        txtNS.setText(Html.fromHtml(matcher.group(1)));
                    }

                }
                if (matcher2.find()) {
                    txtCDATE.setText(matcher2.group(1));
                }

                if (matcher3.find()) {
                    txtEDATE.setText(matcher3.group(1));
                }
                    if (txtCDATE.getText() == "") {
                        pbar.setVisibility(View.GONE);
                        whatdomain.setText(domainname + extDomain + " is not registered.");
                    } else {
                        pbar.setVisibility(View.GONE);
                        whatdomain.setText("Info of " + domainname + extDomain + " : ");
                    }

            } else {
                pbar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Check your network connection", Toast.LENGTH_SHORT).show();
            }


/*            if (!"200".equals(rspnsCode)) {
                pbar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();*/

        }

    }
}
