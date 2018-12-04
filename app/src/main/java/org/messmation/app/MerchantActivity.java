package org.messmation.app;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;
import org.messmation.app.beacon.MessMationApplication;
import org.messmation.app.db.DBAdapter;

import java.util.Date;

public class MerchantActivity extends AppCompatActivity implements PaymentResultListener, View.OnClickListener {

    int coupons;
    double couponPrice;
    double cost;
    TextView textView1, textView2;
    Button button;
    String email, phone, loggedInuserID;
    Intent retIntent;
    DBAdapter dbAdapter;
    Calendar c;

    public static final String TAG = MerchantActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant);
        Checkout.preload(getApplicationContext());  //<-- if possible, call this earlier
        button = findViewById(R.id.paybtn);
        button.setOnClickListener(this);
        coupons = getIntent().getIntExtra("ccount", 20);
        couponPrice = getIntent().getDoubleExtra("couponPrice", 1);
        loggedInuserID = ((MessMationApplication) this.getApplicationContext()).getLoggedinUserUID();
        textView1 = findViewById(R.id.cpncnt);
        textView2 = findViewById(R.id.cpncost);
        textView1.setText("No. of Coupons: " + coupons);
        textView2.setText("INR " + couponPrice);
        email = MessMationApplication.getLoggedinEmail();
    }

    public double calcCost(int n){
        // fetch from server (vendor enters in app which is stored in server)
        return 1.0;
    }

    public void startPayment(){
        Checkout checkout = new Checkout();

        // checkout.setImage(R.drawable.);      // <-- Set App Logo

        final Activity activity = this;
        try{
            JSONObject options = new JSONObject();
            JSONObject prefill = new JSONObject();
            prefill.put("email", email);
            prefill.put("phone","");
            options.put("name", "");     // <-- value = Merchant Name
            options.put("description","");  // <-- description could be order#
            options.put("currency", "INR");
            options.put("amount",couponPrice*100);       // <-- Amount is always passed in PAISE Eg: "500" = Rs 5.00
            options.put("prefill", prefill);
            checkout.open(activity, options);
        }
        catch(Exception e){
            Log.e(TAG,"Error in starting Razorpay Checkout", e);
        }
    }

    // These two methods should not depend on any variable that is not set through lifecycle hooks
    // Test -> Don't Keep Activities" in Developer Options under Settings
    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Payment Successful: " + s, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Exception in onPaymentSuccess" + s);

        // redirect to homepage and disable buying of coupons until next month
        // payment status update at database
        ((MessMationApplication)this.getApplicationContext()).setPaymentStatus(Boolean.TRUE);
        //retIntent = new Intent();
        //retIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //setResult(Activity.RESULT_OK, retIntent);
        //finish();
        c = Calendar.getInstance();
        c.setTime(new Date());
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        dbAdapter = new DBAdapter();
        dbAdapter.creditCoupons(loggedInuserID, coupons, new Date(), c.getTime(),s,couponPrice);
        Intent intent = new Intent(this, PaymentConfirmation.class);
        //intent.putExtra("success", Boolean.TRUE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment failed: " + i + " " + s, Toast.LENGTH_SHORT).show();
        //Log.e(TAG, s);
        if(Checkout.NETWORK_ERROR == i){
            //Toast.makeText(this, "Payment failed: " + i + " " + s, Toast.LENGTH_SHORT).show();
            Log.e(TAG, s);
        }
        else if(Checkout.INVALID_OPTIONS == i){
            //Toast.makeText(this, "Payment failed: " + i + " " + s, Toast.LENGTH_SHORT).show();
            Log.e(TAG, s);
        }
        else if(Checkout.PAYMENT_CANCELED == i){
            //Toast.makeText(this, "Payment failed: " + i + " " + s, Toast.LENGTH_SHORT).show();
            Log.e(TAG, s);
        }
        else if(Checkout.TLS_ERROR == i){
            //Toast.makeText(this, "Payment failed: " + i + " " + s, Toast.LENGTH_SHORT).show();
            Log.e(TAG, s);
        }
        retIntent = new Intent();
        //retIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        setResult(Activity.RESULT_CANCELED, retIntent);
        //finish();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.paybtn){
            startPayment();
        }
    }
}
