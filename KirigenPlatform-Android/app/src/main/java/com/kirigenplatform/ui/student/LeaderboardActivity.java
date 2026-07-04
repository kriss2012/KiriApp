package com.kirigenplatform.ui.student;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.kirigenplatform.R;

public class LeaderboardActivity extends AppCompatActivity {

    private TextView tvReferralLink;
    private Button btnCopyRef;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        tvReferralLink = findViewById(R.id.tv_referral_link);
        btnCopyRef = findViewById(R.id.btn_copy_ref);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());
        
        btnCopyRef.setOnClickListener(v -> {
            String referralLink = tvReferralLink.getText().toString();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("KiriGen Referral Link", referralLink);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Referral link copied to clipboard!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
