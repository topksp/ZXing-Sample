package com.example.ray.myapplication2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ray.myapplication2.util.QRCodeGenerator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.EnumMap;
import java.util.Hashtable;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public final static String TAG = MainActivity.class.getSimpleName();
    private TextView tvScanContent;
    private TextView tvScanFormat;
    private Button btnScan, btnGenerate;
    private EditText etQrcodeContent;
    private CheckBox cbWithLogo;
    private ImageView imgQRCode;
    private IntentIntegrator integrator;
    private int IMAGE_HALFWIDTH = 50;//宽度值，影响中间图片大小

    @Override
    protected void onCreate(Bundle saveInstanceStat) {
        super.onCreate(saveInstanceStat);
        setContentView(R.layout.activity_main);
        initView();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_scan:
                initiateScanning();
                //如有安裝ZXing官方APK可採底下作法
//                IntentIntegrator scanIntegrator = new IntentIntegrator(mainactivity);
//                scanIntegrator.initiateScan();
                break;

            case R.id.btn_generate:
//                genQRCode();
                QRCodeGenerator genQRCode = new QRCodeGenerator();
                if (cbWithLogo.isChecked()){
                    Bitmap bitmap = genQRCode.createQRCodeWithLogo(etQrcodeContent.getText().toString(), 500,
                            BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
                    imgQRCode.setImageBitmap(bitmap);
                }else{
                    Bitmap bitmap = genQRCode.createQRCode(etQrcodeContent.getText().toString(), 500);
                    imgQRCode.setImageBitmap(bitmap);
                }
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {
            String scanContent = scanningResult.getContents();
            String scanFormat = scanningResult.getFormatName();
            tvScanContent.setText(scanContent);
            tvScanFormat.setText(scanFormat);
        } else {
            Toast.makeText(getApplicationContext(), "nothing", Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        tvScanContent = (TextView) findViewById(R.id.tv_scan_content);
        tvScanFormat = (TextView) findViewById(R.id.tv_scan_format);
        etQrcodeContent = (EditText) findViewById(R.id.et_qrcode_content);
        cbWithLogo = (CheckBox) this.findViewById(R.id.cb_with_logo);
        imgQRCode = (ImageView) findViewById(R.id.img_qr_code);
        integrator = new IntentIntegrator(this);
        btnScan = (Button) findViewById(R.id.btn_scan);
        btnGenerate = (Button) findViewById(R.id.btn_generate);
        btnScan.setOnClickListener(this);
        btnGenerate.setOnClickListener(this);
    }

    private void initiateScanning() {
        integrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("請掃描");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    private void genQRCode() {
        // QR code 的內容
        String QRCodeContent = etQrcodeContent.getText().toString();
        // QR code 寬度
        int QRCodeWidth = 156;
        // QR code 高度
        int QRCodeHeight = 156;
        // QR code 內容編碼
        Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        MultiFormatWriter writer = new MultiFormatWriter();
        try {
            // 容錯率姑且可以將它想像成解析度，分為 4 級：L(7%)，M(15%)，Q(25%)，H(30%)
            // 設定 QR code 容錯率為 H
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

            // 建立 QR code 的資料矩陣
            BitMatrix result = writer.encode(QRCodeContent, BarcodeFormat.QR_CODE, QRCodeWidth, QRCodeHeight, hints);
            // ZXing 還可以生成其他形式條碼，如：BarcodeFormat.CODE_39、BarcodeFormat.CODE_93、BarcodeFormat.CODE_128、BarcodeFormat.EAN_8、BarcodeFormat.EAN_13...

            //建立點陣圖
            Bitmap bitmap = Bitmap.createBitmap(QRCodeWidth, QRCodeHeight, Bitmap.Config.ARGB_8888);
            // 將 QR code 資料矩陣繪製到點陣圖上
            for (int y = 0; y < QRCodeHeight; y++) {
                for (int x = 0; x < QRCodeWidth; x++) {
                    bitmap.setPixel(x, y, result.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            ImageView imgView = (ImageView) findViewById(R.id.img_qr_code);
            // 設定為 QR code 影像
            imgView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
