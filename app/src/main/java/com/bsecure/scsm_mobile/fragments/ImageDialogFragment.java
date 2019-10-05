package com.bsecure.scsm_mobile.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.callbacks.IDownloadCallback;

import java.net.URI;

public class ImageDialogFragment extends DialogFragment implements IDownloadCallback {

    private String TAG = ImageDialogFragment.class.getSimpleName();

    private TextView ok, retake;

    private ImageView image_preview, multi, send;

    private String type = "", path;

    public static ImageDialogFragment newInstance() {
        ImageDialogFragment f = new ImageDialogFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_image_preview, container, false);

        image_preview = v.findViewById(R.id.image_preview);

        multi = v.findViewById(R.id.multi);

        send = v.findViewById(R.id.send);

        ok = v.findViewById(R.id.ok);

        retake = v.findViewById(R.id.retake);

        type = getArguments().getString("type");

        if(type.equals("0"))
        {
            multi.setVisibility(View.VISIBLE);

            send.setVisibility(View.VISIBLE);
        }
        else if(type.equals("1"))
        {
            ok.setVisibility(View.VISIBLE);

            retake.setVisibility(View.VISIBLE);

        }

        path = getArguments().getString("path");

        Bitmap bitmap = BitmapFactory.decodeFile(path);

        image_preview.setImageBitmap(bitmap);


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                actionListener listener = (actionListener) getActivity();

                listener.onAction(1);

                dismiss();

            }
        });

        retake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                actionListener listener = (actionListener) getActivity();

                listener.onAction(0);

                dismiss();

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                actionListener listener = (actionListener) getActivity();

                listener.onAction(2);

                dismiss();

            }
        });

        multi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                actionListener listener = (actionListener) getActivity();

                listener.onAction(3);

                dismiss();

            }
        });

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    public void onStateChange(int what, int arg1, int arg2, Object obj, int requestId) {

    }



    @Override
    public void onPause() {
        super.onPause();
        //images.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        //images = simages;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //images.clear();
    }

    public interface actionListener
    {
        void onAction(int i);
    }
}
