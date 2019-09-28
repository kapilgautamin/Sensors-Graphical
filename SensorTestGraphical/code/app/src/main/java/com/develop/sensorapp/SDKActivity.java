package com.develop.sensorapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.DecimalFormat;
import java.util.List;


public class SDKActivity extends Activity {
    DecimalFormat df = new DecimalFormat("###0.0000");

    public enum Sensors {
        //0-26 are from sensors.h
        //26-49 are samsung types
        METADATA(0),
        ACCEL(1),
        MAGNET(2),
        ORIENT(3),
        GYRO(4),
        LIGHT(5),
        PRESSURE(6),
        TEMP(7),
        PROX(8),
        GRAVITY(9),
        ACCELLINEAR(10),
        ROTATION(11),
        HUMIDITY(12),
        TEMPAMB(13),
        MAGNETUNCAL(14),
        GAMEROTATION(15),
        GYROUNCAL(16),
        SIGNIFICANT(17),
        STEPDETECT(18),
        STEPCOUNT(19),
        GEOROTATION(20),
        HEART(21),
        TILT(22),
        WAKE(23),
        GLANCE(24),
        PICKUP(25),
        WRISTTILT(26),
        ULTRAVIOLET(27),            //coded in sensors.h as SENSOR_TYPE_DEVICE_PRIVATE_BASE + 21
        SCREENORIENT(28),           //SENSOR_TYPE_DEVICE_PRIVATE_BASE is 0x10000,ie,65536
        MOTIONRECOG(29),            //sensors defined in samsung are in package com.samsung.<sensor_name>
        GRIP(30),
        BIO(31),
        BIOHRM(32),
        CONTROLMOTIONRECOG(33),
        TABLEROTATION(34),
        UVRAY(35),
        TEMPBODY(36),
        BLOODGLUCOSE(37),
        PEDOMETER(38),
        ELECTROCARDIOGRAM(39),
        BIOLEDIR(40),
        BIOLEDRED(41),
        BIOLEDGREEN(42),
        BIOLEDVIOLET(43),
        GRIPWIFI(44),
        BIOALC(45),
        LIGHTIR(46),
        INTERRUPTGYRO(47),
        HRMPROXDETECT(48),
        MOTORTEST(49);

        private int sensorId;

        Sensors(int sensorId) {
            this.sensorId = sensorId;
        }
    }

    private SensorManager sensorManager;
    private final String TAG = "debugApp";
    private final int sensorOnDevice = 50;

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public int location;
    public int threadLocation;
    public TextView[] sData = new TextView[sensorOnDevice];
    public TextView[] sButton = new TextView[sensorOnDevice];
    public TextView[] sBatch = new TextView[sensorOnDevice];
    public TextView[] sInfo = new TextView[sensorOnDevice];
    public TextView[] sStartStop = new TextView[sensorOnDevice];
    public long[] sprevTimeStamp = new long[sensorOnDevice];
    public long[] sfreqTimeStamp = new long[sensorOnDevice];
    public Thread[] sThread = new Thread[sensorOnDevice];
    public long[] sCount = new long[sensorOnDevice];
    public int[] sState = new int[sensorOnDevice];
    public int[] sDelayValue = new int[sensorOnDevice];
    public int[] sBatchValue = new int[sensorOnDevice];
    public TextView[] sName = new TextView[sensorOnDevice];
    public int sEventCount[] = new int[sensorOnDevice];
    public LineGraphSeries<DataPoint>[] sGraphSeriesX = new LineGraphSeries[sensorOnDevice];
    public LineGraphSeries<DataPoint>[] sGraphSeriesY = new LineGraphSeries[sensorOnDevice];
    public LineGraphSeries<DataPoint>[] sGraphSeriesZ = new LineGraphSeries[sensorOnDevice];
    public LineGraphSeries<DataPoint>[] sGraphSeriesEventTimeStamp = new LineGraphSeries[sensorOnDevice];
    public int[] SGraphLastX = new int[sensorOnDevice];
    public int[] SGraphLastY = new int[sensorOnDevice];
    public int[] SGraphLastZ = new int[sensorOnDevice];
    public int[] SGraphLastEventTS = new int[sensorOnDevice];
    public GraphView[] sGraph = new GraphView[sensorOnDevice];
    public DataPoint[][] xDataPoint = new DataPoint[sensorOnDevice][20];
    public DataPoint[][] yDataPoint = new DataPoint[sensorOnDevice][20];
    public DataPoint[][] zDataPoint = new DataPoint[sensorOnDevice][20];
    public DataPoint EventTSDataPoint = new DataPoint(0, 0);
    public AppCompatCheckBox[] sCheckboxX = new AppCompatCheckBox[sensorOnDevice];
    public AppCompatCheckBox[] sCheckboxY = new AppCompatCheckBox[sensorOnDevice];
    public AppCompatCheckBox[] sCheckboxZ = new AppCompatCheckBox[sensorOnDevice];
    public LinearLayout.LayoutParams[] lparams_sensor = new LinearLayout.LayoutParams[sensorOnDevice];
    public Sensor[] sSensor = new Sensor[sensorOnDevice];
    public SensorEventListener[] sListener = new SensorEventListener[sensorOnDevice];
    public SensorEvent eventListener;
    public int count = 0;
    private final static String STORETEXT = "storeLogs.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sdk_view);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.sdk_view, null);
        //Sensor my=(Sensor) sensorManager.getDefaultSensor(Sensor.TYPE_g);
        sSensor[1] = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sSensor[2] = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sSensor[3] = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sSensor[4] = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sSensor[5] = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sSensor[6] = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        sSensor[7] = sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
        sSensor[8] = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sSensor[9] = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sSensor[10] = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sSensor[11] = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        sSensor[12] = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        sSensor[13] = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        sSensor[14] = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
        sSensor[15] = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        sSensor[16] = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
        sSensor[17] = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
        sSensor[18] = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sSensor[19] = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sSensor[20] = sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        sSensor[21] = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        //sSensor[22]=sensorManager.getDefaultSensor(Sensor.TYPE_TILT_DETECTOR);
        //sSensor[28]=sensorManager.getDefaultSensor(Sensor.TYPE_WAKE_GESTURE);
        sSensor[29] = sensorManager.getDefaultSensor(Sensor.TYPE_MOTION_DETECT);

        LinearLayout layout = (LinearLayout) findViewById(R.id.sensorLayout);

        List<Sensor> sList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < sList.size(); i++) {
            //for(int i=0;i<1;i++){
            // TextView sName=new TextView(this);
            String name = sList.get(i).getName();

            final int location = assign_location(sList.get(i));

            //sNameId.put(name, location);
            Log.e(TAG, i + name + location);

            if (location != -1) {

                LinearLayout layout_sensor = new LinearLayout(this);
                layout_sensor.setOrientation(LinearLayout.VERTICAL);
                lparams_sensor[location] = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                layout_sensor.setLayoutParams(lparams_sensor[location]);
                lparams_sensor[location].height = 160;
                lparams_sensor[location].setMargins(0, 0, 0, 40);

                //Log.e("TAG","coming hre");
                sName[location] = new TextView(this);
                sName[location].setLayoutParams(lparams);
                sName[location].setTextSize(15);
                sName[location].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                sName[location].setText(i + 1 + ". " + sList.get(i).getName());
                layout_sensor.addView(sName[location]);

                /*sName[location].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(sGraph[location].getTranslationY()==0){
                            sGraph[location].animate().translationY(-400);
                            sCheckboxX[location].animate().translationY(-400);
                            sCheckboxY[location].animate().translationY(-400);
                            sCheckboxZ[location].animate().translationY(-400);
                            lparams_sensor[location].height=190;
                        }
                        else{
                            sGraph[location].animate().translationY(0);
                            sCheckboxX[location].animate().translationY(0);
                            sCheckboxY[location].animate().translationY(0);
                            sCheckboxZ[location].animate().translationY(0);
                            lparams_sensor[location].height=400;
                        }

                    }
                });*/

                LinearLayout layout_button = new LinearLayout(this);
                layout_button.setOrientation(LinearLayout.HORIZONTAL);
                layout_button.setLayoutParams(lparams);

                sStartStop[location] = new TextView(this);
                sStartStop[location].setLayoutParams(lparams);
                sStartStop[location].setText("Start");
                sStartStop[location].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                sStartStop[location].setTextColor(Color.WHITE);
                sStartStop[location].setPadding(60, 0, 60, 0);
                sStartStop[location].setTextSize(14);
                sStartStop[location].setBackgroundResource(R.drawable.my_button);
                sStartStop[location].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onToggle(location);
                    }
                });
                sState[location] = 0;
                sprevTimeStamp[location] = 0;
                sfreqTimeStamp[location] = 0;


                sBatchValue[location] = 0;
                sCount[location] = 0;
                layout_button.addView(sStartStop[location]);

                int minDelay = sSensor[location].getMinDelay();
                int maxDelay = sSensor[location].getMaxDelay();
                if (maxDelay == 0) maxDelay = 200000;
                sButton[location] = new TextView(this);
                sButton[location].setLayoutParams(lparams);
                sButton[location].setText("Delay");
                sButton[location].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                sButton[location].setTextColor(Color.WHITE);
                sButton[location].setPadding(60, 0, 60, 0);
                sButton[location].setTextSize(13);
                sButton[location].setBackgroundResource(R.drawable.my_button);
                sButton[location].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onAlertDelay(location);
                    }
                });
                layout_button.addView(sButton[location]);

                sDelayValue[location] = maxDelay;

                sBatch[location] = new TextView(this);
                sBatch[location].setLayoutParams(lparams);
                sBatch[location].setText("Batch");
                sBatch[location].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                sBatch[location].setTextColor(Color.WHITE);
                sBatch[location].setTextSize(13);
                sBatch[location].setPadding(60, 0, 60, 0);
                sBatch[location].setBackgroundResource(R.drawable.my_button);
                sBatch[location].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBatch(location);
                    }
                });
                layout_button.addView(sBatch[location]);

                //add layout_buttons to the main layout
                layout_sensor.addView(layout_button);

                LinearLayout layout_graph = new LinearLayout(this);
                layout_graph.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams lparams_graph = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                lparams_graph.height = 200;
                layout_graph.setLayoutParams(lparams_graph);
                // we get graph view instance
                sGraph[location] = new GraphView(this);
                sGraph[location].setBackgroundColor(Color.LTGRAY);
                // data
                sGraphSeriesX[location] = new LineGraphSeries<DataPoint>();
                sGraphSeriesY[location] = new LineGraphSeries<DataPoint>();
                sGraphSeriesZ[location] = new LineGraphSeries<DataPoint>();
                sGraphSeriesEventTimeStamp[location] = new LineGraphSeries<DataPoint>();

                sGraphSeriesX[location].setColor(Color.GREEN);
                sGraphSeriesY[location].setColor(Color.RED);
                sGraphSeriesZ[location].setColor(Color.BLUE);
                sGraphSeriesEventTimeStamp[location].setColor(Color.WHITE);

                //sGraphSeriesX[location].setSize(4);
                //sGraphSeriesY[location].setSize(4);
                //sGraphSeriesZ[location].setSize(4);
                //sGraphSeriesEventTimeStamp[location].setSize(4);

                sGraphSeriesX[location].setTitle("x");
                sGraphSeriesY[location].setTitle("y");
                sGraphSeriesZ[location].setTitle("z");
                //sGraphSeriesEventTimeStamp[location].setTitle("EventTS");
                sGraph[location].addSeries(sGraphSeriesX[location]);
                sGraph[location].addSeries(sGraphSeriesY[location]);
                sGraph[location].addSeries(sGraphSeriesZ[location]);
                sGraph[location].addSeries(sGraphSeriesEventTimeStamp[location]);

                //sGraph[location].getLegendRenderer().setVisible(true);
                //sGraph[location].getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

                sGraph[location].getGridLabelRenderer().setHorizontalLabelsVisible(false);
                sGraphSeriesX[location].setOnDataPointTapListener(new OnDataPointTapListener() {
                    @Override
                    public void onTap(Series series, DataPointInterface dataPoint) {
                        Toast.makeText(SDKActivity.this, "SeriesX: " + dataPoint.getY(), Toast.LENGTH_SHORT).show();
                    }
                });
                sGraphSeriesY[location].setOnDataPointTapListener(new OnDataPointTapListener() {
                    @Override
                    public void onTap(Series series, DataPointInterface dataPoint) {
                        Toast.makeText(SDKActivity.this, "SeriesY: " + dataPoint.getY(), Toast.LENGTH_SHORT).show();
                    }
                });
                sGraphSeriesZ[location].setOnDataPointTapListener(new OnDataPointTapListener() {
                    @Override
                    public void onTap(Series series, DataPointInterface dataPoint) {
                        Toast.makeText(SDKActivity.this, "SeriesZ: " + dataPoint.getY(), Toast.LENGTH_SHORT).show();
                    }
                });
                sGraphSeriesEventTimeStamp[location].setOnDataPointTapListener(new OnDataPointTapListener() {
                    @Override
                    public void onTap(Series series, DataPointInterface dataPoint) {
                        Toast.makeText(SDKActivity.this, "SeriesEventTS: " + dataPoint.getY(), Toast.LENGTH_SHORT).show();
                    }
                });

                // customize a little bit viewport
                Viewport viewport = sGraph[location].getViewport();
                //viewport.setYAxisBoundsManual(true);
                viewport.setXAxisBoundsManual(true);
                viewport.setMinX(0);
                viewport.setMaxX(200);
                viewport.setScrollable(true);

                viewport.setScalable(true);
                viewport.setScrollableY(true);
                //viewport.setScalableY(true);

                layout_graph.addView(sGraph[location]);

                layout_sensor.addView(layout_graph);
                //layout.addView(layout_sensor);

                ColorStateList colorStateList = new ColorStateList(
                        new int[][]{
                                new int[]{android.R.attr.state_enabled} //enabled
                        },
                        new int[]{getResources().getColor(R.color.white)}
                );

                LinearLayout layout_event = new LinearLayout(this);
                layout_event.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams lparams_event = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
                layout_event.setLayoutParams(lparams_event);
                //layout_check.setWeightSum(3);

                sInfo[location] = new TextView(this);
                sInfo[location].setLayoutParams(lparams);
                sInfo[location].setText("Info");
                sInfo[location].setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                sInfo[location].setTextColor(Color.WHITE);
                sInfo[location].setTextSize(10);
                sInfo[location].setPadding(60, 0, 60, 0);
                sInfo[location].setBackgroundResource(R.drawable.my_button);
                sInfo[location].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onInfo(location);
                    }
                });
                layout_event.addView(sInfo[location]);

                sData[location] = new TextView(this);
                sData[location].setLayoutParams(lparams);
                sData[location].setTextSize(10);
                sData[location].setPadding(60, 0, 60, 0);
                sData[location].setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                // sData[location].setText("\t\t\t\tx: 0\t\t\t\ty: 0\t\t\t\tz: 0");
                sData[location].setText("");
                layout_event.addView(sData[location]);

                layout_sensor.addView(layout_event);

                LinearLayout layout_check = new LinearLayout(this);
                layout_check.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams lparams_check = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
                layout_check.setLayoutParams(lparams_check);
                //layout_check.setWeightSum(3);

                sCheckboxX[location] = new AppCompatCheckBox(this);
                sCheckboxX[location].setText("x:   0       ");
                sCheckboxX[location].setChecked(true);
                sCheckboxX[location].setBackgroundColor(Color.rgb(163, 250, 96));
                sCheckboxX[location].setSupportButtonTintList(colorStateList);
                sCheckboxX[location].setTextSize(10);
                //sCheckboxX[location].setWidth(350);
                sCheckboxX[location].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.3f));
                layout_check.addView(sCheckboxX[location]);

                sCheckboxY[location] = new AppCompatCheckBox(this);
                sCheckboxY[location].setText("y:    0      ");
                sCheckboxY[location].setChecked(true);
                sCheckboxY[location].setBackgroundColor(Color.rgb(247, 100, 140));
                sCheckboxY[location].setSupportButtonTintList(colorStateList);
                sCheckboxY[location].setTextSize(10);
                //sCheckboxY[location].setWidth(350);
                sCheckboxY[location].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.3f));
                layout_check.addView(sCheckboxY[location]);

                sCheckboxZ[location] = new AppCompatCheckBox(this);
                sCheckboxZ[location].setText("z:     0     ");
                sCheckboxZ[location].setChecked(true);
                sCheckboxZ[location].setBackgroundColor(Color.rgb(135, 206, 235));
                sCheckboxZ[location].setSupportButtonTintList(colorStateList);
                sCheckboxZ[location].setTextSize(10);
                //sCheckboxZ[location].setWidth(350);
                sCheckboxZ[location].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 0.3f));
                layout_check.addView(sCheckboxZ[location]);

                sGraph[location].animate().translationY(-400);
                sCheckboxX[location].animate().translationY(-400);
                sCheckboxY[location].animate().translationY(-400);
                sCheckboxZ[location].animate().translationY(-400);

                layout_sensor.addView(layout_check);

                TextView lineSeperator = new TextView(this);
                lineSeperator.setHeight(5);
                lineSeperator.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                lineSeperator.setBackgroundColor(Color.BLACK);
                layout_sensor.addView(lineSeperator);


                layout.addView(layout_sensor);

            }
            else {
                // Log.e("TAG","Logged in");
                TextView notYet = new TextView(this);
                notYet.setLayoutParams(lparams);
                notYet.setBackgroundColor(getResources().getColor(R.color.lightYellow));

                notYet.setText(i + 1 + ". " + name + " not yet Implemented");
                layout.addView(notYet);

                TextView lineSeperator = new TextView(this);
                lineSeperator.setHeight(5);
                lineSeperator.setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
                lineSeperator.setBackgroundColor(Color.BLACK);
                layout.addView(lineSeperator);
            }
        }
        TextView logBox = (TextView) findViewById(R.id.logText);
        logBox.setText("Hi!! I am LogBOX");
    }

    public void onBatch(final int location) {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.batch_value);
        dialog.setTitle("Set Batch(s)");

        RadioButton radio1 = (RadioButton) dialog.findViewById(R.id.Radio1);
        RadioButton radio2 = (RadioButton) dialog.findViewById(R.id.Radio2);
        RadioButton radio3 = (RadioButton) dialog.findViewById(R.id.Radio3);
        RadioButton radio4 = (RadioButton) dialog.findViewById(R.id.Radio4);

        switch (sBatchValue[location]) {
            case 1000000:
                radio1.setChecked(true);
                break;
            case 2000000:
                radio2.setChecked(true);
                break;
            case 5000000:
                radio3.setChecked(true);
                break;
            case 10000000:
                radio4.setChecked(true);
                break;
        }


        final EditText manualEntry = (EditText) dialog.findViewById(R.id.manualBatchText2);
        manualEntry.setEnabled(false);
        Button buttonDialogSet = (Button) dialog.findViewById(R.id.setBatch);
        final Button buttonDialogManual = (Button) dialog.findViewById(R.id.buttonManual2);
        final int[] flag = {0};
        buttonDialogManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroupBatch);
                radioGroup.clearCheck();
                if (flag[0] == 0) {
                    radioGroup.setVisibility(View.GONE);
                    manualEntry.setEnabled(true);
                    manualEntry.setText("");
                    manualEntry.setVisibility(View.VISIBLE);
                    buttonDialogManual.setText("Radio Entry");
                    flag[0] = 1;
                } else {
                    radioGroup.setVisibility(View.VISIBLE);
                    manualEntry.setEnabled(false);
                    manualEntry.setVisibility(View.GONE);
                    buttonDialogManual.setText("Manual Entry");
                    flag[0] = 0;
                }
            }
        });


        buttonDialogSet.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (manualEntry.isEnabled()) {


                    if (manualEntry.getText().toString().matches(""))
                        Toast.makeText(SDKActivity.this, "Please enter a value", Toast.LENGTH_SHORT).show();
                    else {
                        sBatch[location].setText("BATCH(" + Integer.parseInt(manualEntry.getText().toString()) + ")");
                        sBatchValue[location] = Integer.parseInt(manualEntry.getText().toString()) * 1000 * 1000;
                    }
                } else

                {
                    final RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroupBatch);

                    int selectedId = radioGroup.getCheckedRadioButtonId();

                    radioGroup.check(selectedId);
                    RadioButton selectedRadio = (RadioButton) dialog.findViewById(selectedId);
                    //  returnDelay=3;

                    switch (selectedId) {

                        case R.id.Radio1: {
                            sBatchValue[location] = 1 * 1000 * 1000;
                            sBatch[location].setText("BATCH(1)");
                        }
                        break;
                        case R.id.Radio2: {
                            sBatchValue[location] = 2 * 1000 * 1000;
                            sBatch[location].setText("BATCH(2)");
                        }
                        break;
                        case R.id.Radio3: {
                            sBatchValue[location] = 5 * 1000 * 1000;
                            sBatch[location].setText("BATCH(5)");
                        }
                        break;
                        default: {
                            sBatchValue[location] = 5 * 1000 * 1000;
                            sBatch[location].setText("BATCH(10)");
                        }
                    }
                }


                if (sState[location] == 1) {
                    //do someting for location 7,ie,significant motion
                    sensorManager.unregisterListener(sListener[location], sSensor[location]);
                    // sprevTimeStamp[location] = 0;
                    sensorManager.registerListener(sListener[location], sSensor[location], sDelayValue[location], sBatchValue[location]);
                } else


                {
                    sState[location] = 0;
                }
                //Log.e("STATE", sensor.getName() + " " + sensor.getState());
                // Log.e("BEFORE STRING", selectedRadio.getText().toString());
                dialog.dismiss();
            }
        });

        // Log.e("STRING", "selected string is " + selectedRadioString + " returnDelay is " + returnDelay);
        dialog.show();
    }

    public void onToggle(final int location) {

        if (sState[location] == 0) {
            sStartStop[location].setText("Pause");
            sGraph[location].animate().translationY(0);
            sCheckboxX[location].animate().translationY(0);
            sCheckboxY[location].animate().translationY(0);
            sCheckboxZ[location].animate().translationY(0);
            lparams_sensor[location].height = LinearLayout.LayoutParams.WRAP_CONTENT;

            sThread[location] = new Thread(new Runnable() {
                @Override
                public void run() {
                    sListener[location] = new SensorEventListener() {
                        @Override
                        public void onSensorChanged(SensorEvent sensorEvent) {
                            onSensor(sensorEvent);
                        }

                        @Override
                        public void onAccuracyChanged(Sensor sensor, int i) {

                        }
                    };
                }
            });
            sThread[location].start();
            try {
                sThread[location].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.e(TAG, "thread id " + sThread[location].getId() + " and name is " + sThread[location].getName());
            Log.e(TAG, "sListener is " + sListener[location]);

            //sName[location].setBackgroundColor(getResources().getColor(R.color.lightGreen));
            sState[location] = 1;
            int minDelay = sSensor[location].getMinDelay();
            int maxDelay = sSensor[location].getMaxDelay();
            if (maxDelay == 0) maxDelay = 200000;
            sButton[location].setText("DELAY(" + sDelayValue[location] / 1000 + ")");
            sBatch[location].setText("BATCH(" + sBatchValue[location] + ")");
            sprevTimeStamp[location] = 0;
            sfreqTimeStamp[location] = 0;
            sensorManager.registerListener(sListener[location], sSensor[location], sDelayValue[location], sBatchValue[location]);
            Log.e("State", sName[location].getText().toString() + sState[location]);
        } else {
            sStartStop[location].setText("Start");

            //sName[location].setBackgroundColor(getResources().getColor(R.color.lightBlue));
            sState[location] = 0;
            sCount[location] = 0;
            //sData[location].setText("\t\t\t\tx: 0\t\t\ty: 0\t\t\tz: 0");
            sData[location].setText("");
            sCheckboxX[location].setText("x:   0       ");
            sCheckboxY[location].setText("y:   0       ");
            sCheckboxZ[location].setText("z:   0       ");
            //sDelayValue[location]=sSensor[location].getMaxDelay();
            sBatchValue[location] = 0;
            int minDelay = sSensor[location].getMinDelay();
            int maxDelay = sSensor[location].getMaxDelay();
            if (maxDelay == 0) maxDelay = 200000;
            sButton[location].setText("DELAY(" + sDelayValue[location] / 1000 + ")");
            sBatch[location].setText("BATCH");
            sensorManager.unregisterListener(sListener[location], sSensor[location]);
            try {
                sThread[location].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.e("State", sName[location].getText().toString() + sState[location]);
        }
    }


    public int assign_location(Sensor currSensor) {
        int returnId = -1;
        if (currSensor == null)
            return -1;
        try {
            switch (currSensor.getType()) {

                case Sensor.TYPE_LINEAR_ACCELERATION:
                    returnId = Sensors.ACCELLINEAR.sensorId;
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    returnId = Sensors.ACCEL.sensorId;
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    returnId = Sensors.GYRO.sensorId;
                    break;
                case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                    returnId = Sensors.GYROUNCAL.sensorId;
                    break;
                case Sensor.TYPE_PROXIMITY:
                    returnId = Sensors.PROX.sensorId;
                    sCheckboxY[returnId].setVisibility(View.INVISIBLE);
                    sCheckboxZ[returnId].setVisibility(View.INVISIBLE);
                    break;
                case Sensor.TYPE_STEP_DETECTOR:
                    returnId = Sensors.STEPDETECT.sensorId;
                    sCheckboxY[returnId].setVisibility(View.INVISIBLE);
                    sCheckboxZ[returnId].setVisibility(View.INVISIBLE);
                    break;
                case Sensor.TYPE_STEP_COUNTER:
                    returnId = Sensors.STEPCOUNT.sensorId;
                    sCheckboxY[returnId].setVisibility(View.INVISIBLE);
                    sCheckboxZ[returnId].setVisibility(View.INVISIBLE);
                    break;
                case Sensor.TYPE_SIGNIFICANT_MOTION:
                    returnId = Sensors.SIGNIFICANT.sensorId;
                    break;
                case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                    returnId = Sensors.GEOROTATION.sensorId;
                    break;
                case Sensor.TYPE_GAME_ROTATION_VECTOR:
                    returnId = Sensors.GAMEROTATION.sensorId;
                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                    returnId = Sensors.ROTATION.sensorId;
                    break;
                case Sensor.TYPE_ORIENTATION:
                    returnId = Sensors.ORIENT.sensorId;
                    break;
                case Sensor.TYPE_GRAVITY:
                    returnId = Sensors.GRAVITY.sensorId;
                    break;
                case Sensor.TYPE_LIGHT:
                    returnId = Sensors.LIGHT.sensorId;
                    sCheckboxY[returnId].setVisibility(View.INVISIBLE);
                    sCheckboxZ[returnId].setVisibility(View.INVISIBLE);
                    break;
                case Sensor.TYPE_HEART_RATE:
                    returnId = Sensors.HEART.sensorId;
                    sCheckboxY[returnId].setVisibility(View.INVISIBLE);
                    sCheckboxZ[returnId].setVisibility(View.INVISIBLE);
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    returnId = Sensors.MAGNET.sensorId;
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                    returnId = Sensors.MAGNETUNCAL.sensorId;
                    break;
                case Sensor.TYPE_PRESSURE:
                    returnId = Sensors.PRESSURE.sensorId;
                    sCheckboxY[returnId].setVisibility(View.INVISIBLE);
                    sCheckboxZ[returnId].setVisibility(View.INVISIBLE);
                    break;
                case Sensor.TYPE_MOTION_DETECT:
                    returnId = Sensors.MOTIONRECOG.sensorId;
                default:
                    returnId = -1;
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return returnId;
    }

    public void onAlertDelay(final int location) {

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.delay_value);
        dialog.setTitle("Set Delay(msec)");

        RadioButton radio1 = (RadioButton) dialog.findViewById(R.id.normalRadio);
        RadioButton radio2 = (RadioButton) dialog.findViewById(R.id.UIRadio);
        RadioButton radio3 = (RadioButton) dialog.findViewById(R.id.gameRadio);
        RadioButton radio4 = (RadioButton) dialog.findViewById(R.id.fastestRadio);


        int minValue = sSensor[location].getMinDelay();
        int maxValue = sSensor[location].getMaxDelay();
        if (maxValue == 0) maxValue = 200000;

        radio1.setText("Normal(" + maxValue / 1000 + " msec)");
        radio4.setText("Fastest(" + minValue / 1000 + " msec)");

        int delayValue = sDelayValue[location];


        if (delayValue == maxValue)
            radio1.setChecked(true);
        else if (delayValue == 66667)
            radio2.setChecked(true);
        else if (delayValue == 20000)
            radio3.setChecked(true);
        else if (delayValue == minValue)
            radio4.setChecked(true);

        final EditText manualEntry = (EditText) dialog.findViewById(R.id.manualDelayText1);
        Button buttonDialogSet = (Button) dialog.findViewById(R.id.setDelay);
        final Button buttonDialogManual = (Button) dialog.findViewById(R.id.buttonManual1);
        final int[] flag = {0};
        manualEntry.setEnabled(false);
        buttonDialogManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup);
                radioGroup.clearCheck();
                if (flag[0] == 0) {
                    radioGroup.setVisibility(View.GONE);
                    manualEntry.setEnabled(true);
                    manualEntry.setText("");
                    manualEntry.setVisibility(View.VISIBLE);
                    buttonDialogManual.setText("Radio Entry");
                    flag[0] = 1;
                } else {

                    radioGroup.setVisibility(View.VISIBLE);
                    manualEntry.setEnabled(false);
                    //   manualEntry.setText("");
                    manualEntry.setVisibility(View.GONE);
                    buttonDialogManual.setText("Manual Entry");
                    flag[0] = 0;
                }
            }
        });


        buttonDialogSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manualEntry.isEnabled()) {

                    if (manualEntry.getText().toString().matches("") || manualEntry.getText().length() == 0) {
                        String message = "Please enter a value";
                        Toast.makeText(SDKActivity.this, message, Toast.LENGTH_SHORT).show();
                    } else {
                        String entry = manualEntry.getText().toString();
                        int value = Integer.parseInt(entry);
                        int minValue = sSensor[location].getMinDelay();
                        int maxValue = sSensor[location].getMaxDelay();
                        if (maxValue == 0) maxValue = 200000;
                        if (value * 1000 < minValue) {
                            int assign = minValue / 1000;
                            sButton[location].setText("DELAY(" + assign + ")");
                            sDelayValue[location] = assign * 1000;
                        } else if (value * 1000 > maxValue) {
                            int assign = maxValue / 1000;
                            sButton[location].setText("DELAY(" + assign + ")");
                            sDelayValue[location] = assign * 1000;
                        } else {
                            sButton[location].setText("DELAY(" + Integer.parseInt(manualEntry.getText().toString()) + ")");
                            sDelayValue[location] = Integer.parseInt(manualEntry.getText().toString()) * 1000;
                        }
                    }
                } else

                {

                    final RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.radioGroup);
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    RadioButton selectedRadio = (RadioButton) dialog.findViewById(selectedId);
                    //  returnDelay=3;
                    switch (selectedId) {
                        case R.id.UIRadio: {
                            sDelayValue[location] = 66667;
                            sButton[location].setText("DELAY(66)");
                        }
                        break;
                        case R.id.gameRadio: {
                            sDelayValue[location] = 20000;
                            sButton[location].setText("DELAY(20)");

                        }
                        break;
                        case R.id.fastestRadio: {
                            sDelayValue[location] = sSensor[location].getMinDelay();
                            sButton[location].setText("DELAY(" + sDelayValue[location] / 1000 + ")");
                        }
                        break;
                        default: {
                            sDelayValue[location] = sSensor[location].getMaxDelay();
                            if (sDelayValue[location] == 0) sDelayValue[location] = 200000;
                            sButton[location].setText("DELAY(" + sDelayValue[location] / 1000 + ")");
                        }
                    }
                }

                if (sState[location] == 1)

                {
                    // sprevTimeStamp[location] = 0;
                    //    sfreqTimeStamp[location]=0;
                    sensorManager.unregisterListener(sListener[location], sSensor[location]);

                    sensorManager.registerListener(sListener[location], sSensor[location], sDelayValue[location], sBatchValue[location]);


                } else {
                    sState[location] = 0;
                }
                dialog.dismiss();
            }
        });

        // Log.e("STRING", "selected string is " + selectedRadioString + " returnDelay is " + returnDelay);
        dialog.show();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void onInfo(final int location) {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        String reportingMode = "";
        switch (sSensor[location].getReportingMode()) {
            case 0:
                reportingMode = "Continous";
                break;
            case 1:
                reportingMode = "On Change";
                break;
            case 2:
                reportingMode = "One Shot";
                break;
            case 3:
                reportingMode = "Special Trigger";
                break;
        }
        int maxDelay = sSensor[location].getMaxDelay();
        if (maxDelay == 0) maxDelay = 200000;
        String info = "\n" + sName[location].getText().toString() + "\n\n" +
                // "\nMin Delay(us) :"+sSensor[location].getMinDelay()+
                //"\nMax Delay(us) :"+sSensor[location].getMaxDelay()+
                "\nMin Frequency(Hz): " + 1000000 / sSensor[location].getMinDelay() +
                "\nMax Frequency(Hz): " + 1000000 / maxDelay +
                "\nMaxRange: " + sSensor[location].getMaximumRange() +
                "\nPower(ma): " + sSensor[location].getPower() +
                "\nReporting Mode: " + reportingMode +
                "\nResolution " + sSensor[location].getResolution() +
                "\nVersion: " + sSensor[location].getVersion() +
                "  Vendor: " + sSensor[location].getVendor() +
                "\nFIFO Reserved Event Count " + sSensor[location].getFifoReservedEventCount() +
                "\nFIFO Max Event Event Count " + sSensor[location].getFifoMaxEventCount() +
                "\n";
        TextView batchText = new TextView(this);
        batchText.setText(info);
        dialog.setView(batchText);
        dialog.show();
    }

    public synchronized void onSensor(SensorEvent event) {

        Sensor mySensor = event.sensor;
        String name = mySensor.getName();
        location = assign_location(mySensor);
        final float xcoor, ycoor, zcoor;
        // String coor="";
        int type = mySensor.getType();
        Button logButton = (Button) findViewById(R.id.handle);
        //Log.e(TAG, name+" "+event.values[0]+"  "+event.values[1]+"  "+event.values[2]);

        if (type == Sensor.TYPE_STEP_COUNTER || type == Sensor.TYPE_STEP_DETECTOR || type == Sensor.TYPE_PROXIMITY || type == Sensor.TYPE_LIGHT || type == Sensor.TYPE_HEART_RATE || type == Sensor.TYPE_AMBIENT_TEMPERATURE || type == Sensor.TYPE_PRESSURE) {
            // Log.e(TAG, name+" "+event.values[0]);
            xcoor = event.values[0];
            ycoor = 0;
            zcoor = 0;

            sCheckboxX[location].setText("x: " + df.format(xcoor));
            xDataPoint[location][sEventCount[location]] = new DataPoint(SGraphLastX[location]++, xcoor);
            yDataPoint[location][sEventCount[location]] = new DataPoint(SGraphLastY[location]++, ycoor);
            zDataPoint[location][sEventCount[location]] = new DataPoint(SGraphLastZ[location]++, zcoor);

        } else {
            // Log.e(TAG, name+" "+event.values[0]+"  "+event.values[1]+"  "+event.values[2]);
            xcoor = event.values[0];
            ycoor = event.values[1];
            zcoor = event.values[2];
            sCheckboxX[location].setText("x: " + df.format(xcoor));
            sCheckboxY[location].setText("y: " + df.format(ycoor));
            sCheckboxZ[location].setText("z: " + df.format(zcoor));

            //Log.e(TAG,"coming to new thread");
            xDataPoint[location][sEventCount[location]] = new DataPoint(SGraphLastX[location]++, xcoor);
            yDataPoint[location][sEventCount[location]] = new DataPoint(SGraphLastY[location]++, ycoor);
            zDataPoint[location][sEventCount[location]] = new DataPoint(SGraphLastZ[location]++, zcoor);

            //  coor ="\t\t\t\tx: " + df.format(xcoor) + "\t\t\ty: " + df.format(ycoor) + "\t\t\tz: " + df.format(zcoor);
        }
        sEventCount[location]++;
        int delay = sDelayValue[location] / 1000;
        if (delay >= 200 && sEventCount[location] >= 2) {
            sEventCount[location] = 0;
            updateGraph(location, 2);
        } else if (delay >= 66 && delay < 200 && sEventCount[location] >= 6) {
            sEventCount[location] = 0;
            updateGraph(location, 6);
        } else if (delay >= 20 && delay < 66 && sEventCount[location] >= 10) {
            sEventCount[location] = 0;
            updateGraph(location, 10);
        } else if (delay < 20 && sEventCount[location] >= 20) {
            sEventCount[location] = 0;
            updateGraph(location, 20);
        }
        sCount[location]++;
       /* if(sCount[location]>5000){
            sGraphSeriesX[location] = new LineGraphSeries<DataPoint>();
            sGraphSeriesY[location] = new LineGraphSeries<DataPoint>();
            sGraphSeriesZ[location] = new LineGraphSeries<DataPoint>();
        }*/


        long sensortimestamp = event.timestamp;
        long systemTimeStamp = SystemClock.elapsedRealtimeNanos();
        float timeDifference = (sensortimestamp - sprevTimeStamp[location]) / 1000000;     //msec
        float freqTimeDifference;
        if (sBatchValue[location] == 0) {
            freqTimeDifference = ((float) (systemTimeStamp - sfreqTimeStamp[location])) / 1000000f;    //msec
        } else
            freqTimeDifference = sBatchValue[location] / 1000;

        int delayValue = sDelayValue[location] / 1000;
        float sensorFrequency = 1000f / timeDifference;
       /* TextView logBox=(TextView) findViewById(R.id.logText);
        logBox.setSingleLine(false);
        logBox.setTextColor(Color.BLACK);
*/
        //try {
        //  OutputStreamWriter out = new OutputStreamWriter(openFileOutput(STORETEXT, 0));
        float systemFrequency = 1000f / freqTimeDifference;
        DecimalFormat dig3 = new DecimalFormat("###.##");
        sData[location].setText("\t\t\t\t\t TD(E)\t" + timeDifference + " \t\tTD(R)\t"
                + dig3.format(freqTimeDifference) + " \t(msec)");

        //EventTSDataPoint = new DataPoint(SGraphLastEventTS[location]++, timeDifference);
        //  sGraphSeriesEventTimeStamp[location].appendData(EventTSDataPoint , true, 600);

        /*Log.e("Sensor",coor + "\n\t\t\t\t\t TD(E)\t"+timeDifference+" \t\tTD(R)\t"
                +dig3.format(freqTimeDifference) +" \t(msec)");
        String message1="\n"+count+". "+name + "\nTD\t(E): " + dig3.format(timeDifference) +" (R): "+ dig3.format(freqTimeDifference)+ " after "+sCount[location]+" events";
        Log.e("PROBLEM_LOG", message1);



        if(freqTimeDifference>2.2*timeDifference ) {
            count++;
            String message="\n"+count+". "+name +"\nDelay\t(E): " + dig3.format(timeDifference) + " (R): "
                    + dig3.format(freqTimeDifference)+" after "+sCount[location]+"    events";
            if(count<=100) {
                //logBox.append(message);
                //logButton.setText("LOG(" + count + ")");
            }
            //                out.append(message);
            Log.e("PROBLEM", message);
        }

else {
            if (systemFrequency < (0.8 * sensorFrequency) || systemFrequency > (2.2 * sensorFrequency)) {
                count++;
                String message = "\n" + count + ". " + name + "\nTD \t(E): " + dig3.format(timeDifference) + " (R): " + dig3.format(freqTimeDifference) + " after " + sCount[location] + " events";
                if (count <= 100) {
                   // logBox.append(message);
                   // logButton.setText("LOG(" + count + ")");
                }
//                    out.append(message);
                Log.e("PROBLEM", message);
            }


            if (systemTimeStamp < sensortimestamp || (systemTimeStamp - sensortimestamp) / 1000000 > 500) {
                count++;
                String message = "\n" + count + ". " + name + "\nTimestamp\t(E): " + sensortimestamp + " (R):                                 " + systemTimeStamp;
                if (count <= 100) {
                    //logBox.append(message);
                   // logButton.setText("LOG(" + count + ")");
                }
                //          out.append(message);
            }
            //    out.close();
        }
        //}
        //catch (Throwable t){
         //   Toast.makeText(this, "Exception: "+t.toString(), Toast.LENGTH_LONG)
          //          .show();
        //}
*/
        sprevTimeStamp[location] = sensortimestamp;
        sfreqTimeStamp[location] = systemTimeStamp;
    }

    public void resetLogs(View v) {
        TextView logText = (TextView) findViewById(R.id.logText);
        Button logButton = (Button) findViewById(R.id.handle);
        count = 0;
        logButton.setText("LOG(" + count + ")");
        logText.setText("Hi!! I am LogBOX");
    }

    public void updateGraph(int location, int updateValue) {

        for (int i = 0; i < updateValue; i++) {
            if (sCheckboxX[location].isChecked())
                sGraphSeriesX[location].appendData(xDataPoint[location][i], true, 500);
            if (sCheckboxY[location].isChecked())
                sGraphSeriesY[location].appendData(yDataPoint[location][i], true, 500);
            if (sCheckboxZ[location].isChecked())
                sGraphSeriesZ[location].appendData(zDataPoint[location][i], true, 500);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "Activity Paused");
        sensorManager.unregisterListener(sListener[location]);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "Activity Resumed");
        for (int i = 0; i < sensorOnDevice; i++)
            if (sState[i] == 1 && assign_location(sSensor[i]) != 7) {
                sensorManager.registerListener(sListener[location], sSensor[i], sDelayValue[i], sBatchValue[i]);
            }
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "Activity Stopped");
    }

}
