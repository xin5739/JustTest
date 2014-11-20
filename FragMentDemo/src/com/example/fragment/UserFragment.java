package com.example.fragment;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.fragmentdemo.R;

import de.greenrobot.event.EventBus;

public class UserFragment extends Fragment{
    Button btn;
    
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, null, false);
        btn=(Button)view.findViewById(R.id.fragment_btn);
        btn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                btn.setText("∑¢ÀÕ");
                EventBus.getDefault().post("aaaa");
            }
        });
        return view;
    }

    public void setBtnText(String str){
        btn.setText("Ω” ’");
    }
    
    
}
