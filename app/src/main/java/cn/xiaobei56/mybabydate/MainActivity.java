package cn.xiaobei56.mybabydate;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    /**
     *
     */
    public static final String BIRTH_DATE = "BIRTH_DATE";
    TextView mTvShow;
    EditText mEtInput;
    Button btnAuto;
    String result;
    boolean isAuto = false;
    Timer mTimer;
    private SimpleDateFormat df;
    private Date date;
    private Calendar calendar;
    private long input;
    private long current;
    private long dur;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    setDate((CharSequence) msg.obj);
                    break;
                default:
                    break;
            }
        }

        ;
    };
    private SharedPreferences myBabyBirth;
    private SharedPreferences.Editor edit;
    private String birth_date;
    private TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvShow = findViewById(R.id.tv_show);
        mEtInput = findViewById(R.id.et_date);
        btnAuto = findViewById(R.id.btn_auto_update);
        myBabyBirth = getSharedPreferences("MyBabyBirth", MODE_PRIVATE);
        edit = myBabyBirth.edit();
        birth_date = myBabyBirth.getString(BIRTH_DATE, "");
        if (TextUtils.isEmpty(birth_date)) {
            isAuto = false;
        } else {
            isAuto = true;
            aotoShow();
        }

        timerTask = new TimerTask() {

            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                msg.obj = birth_date;
                handler.sendMessage(msg);
            }
        };


//        if (!TextUtils.isEmpty(birth_date)) {
//            // TODO: 2021/10/5 开启
//            new Timer().schedule(new TimerTask() {
//                @Override
//                public void run() {
//                    setDate(birth_date);
//                }
//            }, 1000);
//        }

        btnAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aotoShow();
            }
        });
        mEtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null) {
                    if (s.length() == 8 || s.length() == 12) {

                        aotoShow(false);
                        if (edit != null) {
                            edit.putString(BIRTH_DATE, s.toString());
                            edit.commit();
                        }
                        setDate(s);
                        aotoShow(true);

                    }

                }
            }
        });

    }

    private void aotoShow(boolean isAuto) {
        isAuto = isAuto;
        aotoShow();

    }

    private void aotoShow() {
        if (isAuto) {
            isAuto = false;
            if (btnAuto != null) {
                btnAuto.setText("自动更新");
            }
            if (mTimer != null) {
                mTimer.cancel();
            }

        } else {
            isAuto = true;
            if (btnAuto != null) {
                btnAuto.setText("停止自动更新");
            }
            birth_date = myBabyBirth.getString(BIRTH_DATE, "");
            if (!TextUtils.isEmpty(birth_date)) {
                if (mTimer == null) {
                    mTimer = new Timer();
                }
                //5s响一次
                mTimer.schedule(timerTask, 0, 200);
            }

        }
    }

    private void setDate(CharSequence s) {

        try {
            df = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                if (s.length() == 8) {
                    df = new SimpleDateFormat("yyyyMMdd", Locale.getDefault(Locale.Category.FORMAT));
                } else {
                    df = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault(Locale.Category.FORMAT));
                }
            }
            date = df != null ? df.parse(s.toString()) : null;
            if (date != null) {
                calendar = Calendar.getInstance();
                calendar.setTime(date);
                input = calendar.getTimeInMillis();
                calendar.setTime(new Date());
                current = calendar.getTimeInMillis();
                dur = current - input;
                BigDecimal day = new BigDecimal(dur / (1000 * 60 * 60 * 24 * 1.0));
                BigDecimal hour = new BigDecimal(dur % (1000 * 60 * 60 * 24 * 1.0) / 1000 / 60 / 60);
                BigDecimal mina = new BigDecimal(dur % (1000 * 60 * 60 * 24 * 1.0) % (1000 * 60 * 60) / 1000 / 60);
                BigDecimal second = new BigDecimal(dur % (1000 * 60 * 60 * 24 * 1.0) % (1000 * 60 * 60) % (1000 * 60) / 1000);
                mTvShow.setText(day.toBigInteger().toString() + "天" + hour.toBigInteger() + "时" + mina.toBigInteger() + "分" + second.toBigInteger() + "秒");
//                                int min = dur % (1000 * 60 * 60 * 24) /
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}