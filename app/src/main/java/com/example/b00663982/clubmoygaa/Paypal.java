package com.example.b00663982.clubmoygaa;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.b00663982.clubmoygaa.Config.Config;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

import static com.paypal.android.sdk.bl.S;

public class Paypal extends AppCompatActivity {

    private static final int PAYPAL_REQUEST_CODE = 7171;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);

    Button payment;
    EditText donation;

    String amount="";

    @Override
    protected void onDestroy()
    {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paypal);

        //Starting Paypal
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        startService(intent);

        payment = (Button) findViewById(R.id.payment);
        donation = (EditText) findViewById(R.id.donation);

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processPayment();
            }
        });

    }

    private void processPayment() {
        amount = donation.getText().toString();
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), "GBP", "Donation for Moy GAA", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT,payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
       if(requestCode == PAYPAL_REQUEST_CODE)
       {
           if(resultCode == RESULT_OK)
           {
               PaymentConfirmation confirmation =  data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
               if(confirmation != null)
               {
                   try {
                       String paymentDetails = confirmation.toJSONObject().toString(4);

                       startActivity(new Intent(this, PaymentDetails.class)
                               .putExtra("PaymentDetails", paymentDetails)
                               .putExtra("Payment Amount",amount));


                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }


           }

           else if(resultCode == Activity.RESULT_CANCELED)
               Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();

       }
       else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
           Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();
    }
}
