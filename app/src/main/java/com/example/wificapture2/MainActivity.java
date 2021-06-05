package com.example.wificapture2;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import com.example.wificapture2.pocket.SubnetDevices;
import com.example.wificapture2.subnet.Device;
import com.example.wificapture2.ping.PingResult;
import com.example.wificapture2.ping.PingStats;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private TextView resultText;
    private EditText editIpAddress;
    private ScrollView scrollView;
    private Button pingButton;
    private Button wolButton;
    private Button portScanButton;
    private Button subnetDevicesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //toolbar.setBackgroundColor(6);
        //setSupportActionBar(toolbar);

        resultText = findViewById(R.id.resultText);
        //Button subnetcancel=findViewById(R.id.subnetcancel);
        scrollView = findViewById(R.id.scrollView1);
        subnetDevicesButton = findViewById(R.id.subnetDevicesButton);
        textView=(TextView)findViewById(R.id.textView);

        InetAddress ipAddress = IPTools.getLocalIPv4Address();






    findViewById(R.id.subnetDevicesButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            findSubnetDevices();
                            //doPing();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

    }

    private void appendResultsText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultText.append(text + "\n");
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        });
    }

    private void setEnabled(final View view, final boolean enabled) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (view != null) {
                    view.setEnabled(enabled);
                }
            }
        });
    }

    private void doPing(String ip)  { //throws Exception
        //String ipAddress = editIpAddress.getText().toString();
        String ipAddress = ip.toString();
        if (TextUtils.isEmpty(ipAddress)) {
            appendResultsText("Invalid Ip Address");
            return;
        }

        setEnabled(pingButton, false);

        // Perform a single synchronous ping
        PingResult pingResult = null;
        try {
            pingResult = Ping.onAddress(ipAddress).setTimeOutMillis(1000).doPing();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            appendResultsText(e.getMessage());
            setEnabled(pingButton, true);
            return;
        }


        appendResultsText("Pinging Address: " + pingResult.getAddress().getHostAddress());
        appendResultsText("HostName: " + pingResult.getAddress().getHostName());
        appendResultsText(String.format("%.2f ms", pingResult.getTimeTaken()));


        // Perform an asynchronous ping
        Ping.onAddress(ipAddress).setTimeOutMillis(1000).setTimes(1).doPing(new Ping.PingListener() {
            @Override
            public void onResult(PingResult pingResult) {
                if (pingResult.isReachable) {
                    appendResultsText(String.format("%.2f ms", pingResult.getTimeTaken()));
                } else {
                    appendResultsText(getString(R.string.timeout));
                }
            }

            @Override
            public void onFinished(PingStats pingStats) {
                appendResultsText(String.format("Pings: %d, Packets lost: %d",
                        pingStats.getNoPings(), pingStats.getPacketsLost()));
                appendResultsText(String.format("Min/Avg/Max Time: %.2f/%.2f/%.2f ms",
                        pingStats.getMinTimeTaken(), pingStats.getAverageTimeTaken(), pingStats.getMaxTimeTaken()));
                //setEnabled(pingButton, true);
            }

            @Override
            public void onError(Exception e) {
                // TODO: STUB METHOD
                setEnabled(pingButton, true);
            }
        });

    }
    List<String> devFo= new ArrayList<String>();
    public void findSubnetDevices() {

        setEnabled(subnetDevicesButton, false);

        final long startTimeMillis = System.currentTimeMillis();

        SubnetDevices subnetDevices = SubnetDevices.fromLocalAddress().findDevices(new SubnetDevices.OnSubnetDeviceFound() {
            @Override
            public void onDeviceFound(Device device) {
                appendResultsText("Device: " + device.ip+" "+ device.hostname);
                //Store(device.ip);
                //ArrayList<String> devFo= new ArrayList<>();
                devFo.add(device.ip);

            }

            @Override
            public void onFinished(ArrayList<Device> devicesFound) {
                float timeTaken =  (System.currentTimeMillis() - startTimeMillis)/1000.0f;
                appendResultsText("Devices Found: " + devicesFound.size());
                appendResultsText("Finished "+timeTaken+" s");
                Store(devFo);
                textView.setText("Device Count " + String.valueOf(devicesFound.size()));
                setEnabled(subnetDevicesButton, true);
            }
        });
        //textView.setText(subnetDevicesButton.getText().toString());

        // Below is example of how to cancel a running scan
        // subnetDevices.cancel();

    }
    public  void Store(List dev){
        //ArrayList<String> lip=new ArrayList<>();
        //lip.add(dev);
        ///String tst=String.valueOf(lip.size());
        //if(textView.getText() ==tst) {
          //  doPing(lip.get(0));
        //}
        //for(int i=0;i<=lip.size();i++){
        //textView.setText("Device Count " + String.valueOf(lip.size()));
        //}
        //textView.setText("Device Count " + dev.size());---This Array list will provide Device count but it will increase if subnet device button is again clicked
        for(int i=0;i<dev.size();i++) {
            doPing(dev.get(i).toString());
        }
    }



}
