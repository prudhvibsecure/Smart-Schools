package com.bsecure.scsm_mobile.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.SparseBooleanArray;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;


import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.database.DB_Tables;
import com.bsecure.scsm_mobile.models.MessageObject;
import com.bsecure.scsm_mobile.models.UserRepo;
import com.bsecure.scsm_mobile.utils.ContactUtils;
import com.bsecure.scsm_mobile.utils.Utils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Admin on 2018-12-04.
 */

public class MsgReadListAdapter extends RecyclerView.Adapter<MsgReadListAdapter.ContactViewHolder> {

    private Context context = null;
    private View.OnClickListener onClickListener;
    private ChatAdapterListener listener;
    ArrayList<String> mediaList;
    private List<MessageObject> matchesList;
    MediaPlayer mediaPlayer = new MediaPlayer();
    boolean wasPlaying = false;
    private SparseBooleanArray selectedItems;
    private SparseBooleanArray animationItemsIndex;
    private boolean reverseAllAnimations = false;
    private static int currentSelectedIndex = -1;

    public MsgReadListAdapter(List<MessageObject> list, Context context, ChatAdapterListener listener, ArrayList<String> mediaList) {
        this.context = context;
        this.listener = listener;
        this.matchesList = list;
        this.mediaList = mediaList;
        selectedItems = new SparseBooleanArray();
        animationItemsIndex = new SparseBooleanArray();
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public int getItemCount() {

        int arr = 0;

        try {
            if (matchesList.size() == 0) {
                arr = 0;

            } else {
                arr = matchesList.size();
            }

        } catch (Exception e) {
        }
        return arr;

    }

    @Override
    public void onBindViewHolder(final ContactViewHolder contactViewHolder, int position) {

        try {
            DB_Tables db_tables = new DB_Tables(context);
            db_tables.openDB();
            final MessageObject chatList = matchesList.get(position);
            contactViewHolder.my_header_date.setText(ContactUtils.getTimeAgolatest(Long.parseLong(chatList.getMessage_date())));
            if (position > 0) {
                String date_one = Utils.getDate(Long.parseLong(matchesList.get(position).getMessage_date()));
                String date_two = Utils.getDate(Long.parseLong(matchesList.get(position - 1).getMessage_date()));
                if (date_one.equals(date_two)) {
                    contactViewHolder.d_header.setVisibility(View.GONE);
                } else {
                    contactViewHolder.d_header.setVisibility(View.VISIBLE);
                }
            } else {
                contactViewHolder.d_header.setVisibility(View.VISIBLE);
            }
            if (chatList.getUser_me().equals("1")) {
                if (chatList.getForward_status().equalsIgnoreCase("1")) {
                    contactViewHolder.reply_l1.setVisibility(View.GONE);
                    String myType = Utils.getMimeType(matchesList.get(position).getMessage());
                    if (myType == null) {
                        contactViewHolder.chat_left_msg_layout.setVisibility(View.GONE);
                        contactViewHolder.chat_right_msg_layout.setVisibility(View.VISIBLE);
                        contactViewHolder.tk_name.setText(Html.fromHtml(chatList.getSender_name()));
                        contactViewHolder.fd_text_rc.setVisibility(View.VISIBLE);
                        contactViewHolder.chat_left_Image.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);

                        contactViewHolder.chat_right_Image.setVisibility(View.GONE);
                        contactViewHolder.chat_left_Image.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        contactViewHolder.doc_left.setVisibility(View.GONE);

                        contactViewHolder.fd_doc_rt.setVisibility(View.GONE);
                        contactViewHolder.txtMessage_rec.setText(Html.fromHtml(chatList.getMessage()));
                        contactViewHolder.timestamp_rec.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                        //contactViewHolder.tk_name.setText(getDate(Long.parseLong(chatList.getSender_name())));
                    } else if (myType.startsWith("image/")) {
                        contactViewHolder.chat_right_msg_layout.setVisibility(View.GONE);
                        contactViewHolder.chat_left_msg_layout.setVisibility(View.GONE);

                        contactViewHolder.chat_left_Image.setVisibility(View.VISIBLE);
                        contactViewHolder.chat_right_Image.setVisibility(View.GONE);

                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);
                        contactViewHolder.doc_left.setVisibility(View.GONE);

                        contactViewHolder.doc_left.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        contactViewHolder.fd_img_rt.setVisibility(View.VISIBLE);
                        contactViewHolder.vi_play_r.setVisibility(View.GONE);
                        contactViewHolder.vi_play.setVisibility(View.GONE);
                        contactViewHolder.tk_name_img.setText(Html.fromHtml(chatList.getSender_name()));
                        Glide.with(context).load(Paths.up_load + chatList.getMessage()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(contactViewHolder.leftImgeMessage_rec);
                        contactViewHolder.timestamp_rec_img.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                        // contactViewHolder.tk_name_img.setText(getDate(Long.parseLong(chatList.getSender_name())));
                    } else if (myType.startsWith("video/")) {
                        contactViewHolder.chat_right_msg_layout.setVisibility(View.GONE);
                        contactViewHolder.chat_left_msg_layout.setVisibility(View.GONE);
                        contactViewHolder.vi_play_r.setVisibility(View.VISIBLE);

                        contactViewHolder.chat_left_Image.setVisibility(View.VISIBLE);
                        contactViewHolder.chat_right_Image.setVisibility(View.GONE);

                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);
                        contactViewHolder.doc_left.setVisibility(View.GONE);

                        contactViewHolder.doc_left.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        contactViewHolder.fd_img_rt.setVisibility(View.VISIBLE);
                        contactViewHolder.tk_name_img.setText(Html.fromHtml(chatList.getSender_name()));
                        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

                        mediaMetadataRetriever.setDataSource(Paths.up_load + chatList.getMessage(), new HashMap<String, String>());
                        Bitmap bmFrame = mediaMetadataRetriever.getFrameAtTime(1000);
                        contactViewHolder.leftImgeMessage_rec.setImageBitmap(bmFrame);
                        contactViewHolder.timestamp_rec_img.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                        //contactViewHolder.tk_name_img.setText(getDate(Long.parseLong(chatList.getSender_name())));
                    } else if (myType.startsWith("audio/")) {
                        contactViewHolder.chat_right_msg_layout.setVisibility(View.GONE);
                        contactViewHolder.chat_left_msg_layout.setVisibility(View.GONE);

                        contactViewHolder.chat_left_voice.setVisibility(View.VISIBLE);

                        contactViewHolder.doc_left.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);

                        contactViewHolder.chat_left_Image.setVisibility(View.GONE);
                        contactViewHolder.chat_right_Image.setVisibility(View.GONE);

                        //contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);

                        contactViewHolder.fd_voice_lt.setVisibility(View.VISIBLE);
                        final String url_voice = Paths.up_load + chatList.getMessage();
                        contactViewHolder.voice_time_rec.setText(getDate(Long.parseLong(chatList.getMessage_date())));
//                        contactViewHolder.ply_btn_rec.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                try {
//
//
//                                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//                                        clearMediaPlayer();
//                                        contactViewHolder.seekBar_rec.setProgress(0);
//                                        wasPlaying = true;
//                                        contactViewHolder.ply_btn_rec.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.play_btn));
//                                    }
//
//
//                                    if (!wasPlaying) {
//
//                                        if (mediaPlayer == null) {
//                                            mediaPlayer = new MediaPlayer();
//                                        }
//
//                                        contactViewHolder.ply_btn_rec.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.pause_btn));
//                                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                                        mediaPlayer.setDataSource(url_voice);
//
//                                        mediaPlayer.prepare();
//                                        mediaPlayer.setVolume(0.5f, 0.5f);
//                                        mediaPlayer.setLooping(false);
//                                        contactViewHolder.seekBar_rec.setMax(mediaPlayer.getDuration());
//
//                                        mediaPlayer.start();
//                                        new Thread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                int currentPosition = mediaPlayer.getCurrentPosition();
//                                                int total = mediaPlayer.getDuration();
//
//
//                                                while (mediaPlayer != null && mediaPlayer.isPlaying() && currentPosition < total) {
//                                                    try {
//                                                        Thread.sleep(1000);
//                                                        currentPosition = mediaPlayer.getCurrentPosition();
//                                                    } catch (InterruptedException e) {
//                                                        return;
//                                                    } catch (Exception e) {
//                                                        return;
//                                                    }
//
//                                                    contactViewHolder.seekBar_rec.setProgress(currentPosition);
//
//                                                }
//                                            }
//                                        }).start();
//
//                                    }
//
//                                    wasPlaying = false;
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//
//                                }
//                            }
//                        });
//
//
//                        contactViewHolder.seekBar_rec.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                            @Override
//                            public void onStartTrackingTouch(SeekBar seekBar) {
//
//                                contactViewHolder.timer_t_rec.setVisibility(View.VISIBLE);
//                            }
//
//                            @Override
//                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
//                                contactViewHolder.timer_t_rec.setVisibility(View.VISIBLE);
//                                int x = (int) Math.ceil(progress / 1000f);
//
//                                if (x < 10) {
//                                    contactViewHolder.timer_t_rec.setText("0:0" + x);
//                                } else {
//                                    contactViewHolder.timer_t_rec.setText("0:" + x);
//                                }
//                                double percent = progress / (double) seekBar.getMax();
//                                int offset = seekBar.getThumbOffset();
//                                int seekWidth = seekBar.getWidth();
//                                int val = (int) Math.round(percent * (seekWidth - 2 * offset));
//                                int labelWidth = contactViewHolder.timer_t_rec.getWidth();
//                                contactViewHolder.timer_t_rec.setX(offset + seekBar.getX() + val
//                                        - Math.round(percent * offset)
//                                        - Math.round(percent * labelWidth / 2));
//
//                                if (progress > 0 && mediaPlayer != null && !mediaPlayer.isPlaying()) {
//                                    clearMediaPlayer();
//                                    contactViewHolder.ply_btn_rec.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.play_btn));
//                                    seekBar.setProgress(0);
//                                }
//
//                            }
//
//                            @Override
//                            public void onStopTrackingTouch(SeekBar seekBar) {
//
//
//                                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//                                    mediaPlayer.seekTo(seekBar.getProgress());
//                                }
//                            }
//                        });
                        contactViewHolder.ply_btn_rec.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Uri uri = Uri.parse(url_voice);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                intent.setDataAndType(uri, "audio/*");
                                context.startActivity(intent);
                            }
                        });

                    } else {
                        contactViewHolder.doc_left.setVisibility(View.VISIBLE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        contactViewHolder.chat_right_msg_layout.setVisibility(View.GONE);
                        contactViewHolder.chat_left_msg_layout.setVisibility(View.GONE);
                        contactViewHolder.chat_left_Image.setVisibility(View.GONE);
                        contactViewHolder.chat_right_Image.setVisibility(View.GONE);

                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);

                        contactViewHolder.fd_doc_rt.setVisibility(View.VISIBLE);
                        contactViewHolder.timestamp_doc_rec.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                        contactViewHolder.doc_name_rec.setText(chatList.getMessage());
                    }
                } else {
                    String myType = Utils.getMimeType(matchesList.get(position).getMessage());

                    if (myType == null) {
                        contactViewHolder.txtMessage_rec.setText(Html.fromHtml(chatList.getMessage()));
                        contactViewHolder.tk_name.setText(Html.fromHtml(chatList.getSender_name()));
                        contactViewHolder.timestamp_rec.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                        contactViewHolder.txtMessage_send.setVisibility(View.GONE);
                        contactViewHolder.chat_left_msg_layout.setVisibility(View.GONE);
                        contactViewHolder.reply_l1.setVisibility(View.GONE);
                        contactViewHolder.fd_text.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.chat_right_msg_layout.setVisibility(View.VISIBLE);
                        contactViewHolder.vi_play.setVisibility(View.GONE);
                        contactViewHolder.doc_left.setVisibility(View.GONE);

                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        contactViewHolder.chat_right_Image.setVisibility(View.GONE);
                        contactViewHolder.chat_left_Image.setVisibility(View.GONE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);

                    } else if (myType.startsWith("image/")) {
                        contactViewHolder.chat_right_msg_layout.setVisibility(View.GONE);
                        contactViewHolder.chat_left_msg_layout.setVisibility(View.GONE);
                        contactViewHolder.chat_left_Image.setVisibility(View.VISIBLE);
                        contactViewHolder.vi_play.setVisibility(View.GONE);
                        contactViewHolder.doc_left.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);

                        contactViewHolder.vi_play_r.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        contactViewHolder.chat_right_Image.setVisibility(View.GONE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);

                        Glide.with(context).load(Paths.up_load + chatList.getMessage()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(contactViewHolder.leftImgeMessage_rec);
                        contactViewHolder.timestamp_rec_img.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                        contactViewHolder.tk_name_img.setText(Html.fromHtml(chatList.getSender_name()));
                    } else if (myType.startsWith("video/")) {
                        contactViewHolder.chat_right_msg_layout.setVisibility(View.GONE);
                        contactViewHolder.chat_left_msg_layout.setVisibility(View.GONE);
                        contactViewHolder.chat_left_Image.setVisibility(View.VISIBLE);
                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.doc_left.setVisibility(View.GONE);

                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        contactViewHolder.chat_right_Image.setVisibility(View.GONE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);
                        contactViewHolder.vi_play_r.setVisibility(View.VISIBLE);
                        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();

                        mediaMetadataRetriever.setDataSource(Paths.up_load + chatList.getMessage(), new HashMap<String, String>());
                        Bitmap bmFrame = mediaMetadataRetriever.getFrameAtTime(1000);
                        contactViewHolder.leftImgeMessage_rec.setImageBitmap(bmFrame);
                        contactViewHolder.timestamp_rec_img.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                        contactViewHolder.tk_name_img.setText(Html.fromHtml(chatList.getSender_name()));
                    } else if (myType.startsWith("audio/")) {
                        contactViewHolder.chat_right_msg_layout.setVisibility(View.GONE);
                        contactViewHolder.chat_left_msg_layout.setVisibility(View.GONE);
                        contactViewHolder.chat_left_Image.setVisibility(View.GONE);
                        contactViewHolder.doc_left.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.VISIBLE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        contactViewHolder.chat_right_Image.setVisibility(View.GONE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);

                        final String url_voice = Paths.up_load + chatList.getMessage();
                        contactViewHolder.voice_time_rec.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                        contactViewHolder.ply_btn_rec.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Uri uri = Uri.parse(url_voice);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                intent.setDataAndType(uri, "audio/*");
                                context.startActivity(intent);

                            }
                        });
//                        contactViewHolder.ply_btn_rec.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                try {
//
//
//                                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//                                        clearMediaPlayer();
//                                        contactViewHolder.seekBar_rec.setProgress(0);
//                                        wasPlaying = true;
//                                        contactViewHolder.ply_btn_rec.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.play_btn));
//                                    }
//
//
//                                    if (!wasPlaying) {
//
//                                        if (mediaPlayer == null) {
//                                            mediaPlayer = new MediaPlayer();
//                                        }
//
//                                        contactViewHolder.ply_btn_rec.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.pause_btn));
//                                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                                        mediaPlayer.setDataSource(url_voice);
//
//                                        mediaPlayer.prepare();
//                                        mediaPlayer.setVolume(0.5f, 0.5f);
//                                        mediaPlayer.setLooping(false);
//                                        contactViewHolder.seekBar_rec.setMax(mediaPlayer.getDuration());
//
//                                        mediaPlayer.start();
//                                        new Thread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                int currentPosition = mediaPlayer.getCurrentPosition();
//                                                int total = mediaPlayer.getDuration();
//
//
//                                                while (mediaPlayer != null && mediaPlayer.isPlaying() && currentPosition < total) {
//                                                    try {
//                                                        Thread.sleep(1000);
//                                                        currentPosition = mediaPlayer.getCurrentPosition();
//                                                    } catch (InterruptedException e) {
//                                                        return;
//                                                    } catch (Exception e) {
//                                                        return;
//                                                    }
//
//                                                    contactViewHolder.seekBar_rec.setProgress(currentPosition);
//
//                                                }
//                                            }
//                                        }).start();
//
//                                    }
//
//                                    wasPlaying = false;
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//
//                                }
//                            }
//                        });
//
//
//                        contactViewHolder.seekBar_rec.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                            @Override
//                            public void onStartTrackingTouch(SeekBar seekBar) {
//
//                                contactViewHolder.timer_t_rec.setVisibility(View.VISIBLE);
//                            }
//
//                            @Override
//                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
//                                contactViewHolder.timer_t_rec.setVisibility(View.VISIBLE);
//                                int x = (int) Math.ceil(progress / 1000f);
//
//                                if (x < 10)
//                                    contactViewHolder.timer_t_rec.setText("0:0" + x);
//                                else
//                                    contactViewHolder.timer_t_rec.setText("0:" + x);
//
//                                double percent = progress / (double) seekBar.getMax();
//                                int offset = seekBar.getThumbOffset();
//                                int seekWidth = seekBar.getWidth();
//                                int val = (int) Math.round(percent * (seekWidth - 2 * offset));
//                                int labelWidth = contactViewHolder.timer_t_rec.getWidth();
//                                contactViewHolder.timer_t_rec.setX(offset + seekBar.getX() + val
//                                        - Math.round(percent * offset)
//                                        - Math.round(percent * labelWidth / 2));
//
//                                if (progress > 0 && mediaPlayer != null && !mediaPlayer.isPlaying()) {
//                                    clearMediaPlayer();
//                                    contactViewHolder.ply_btn_rec.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.play_btn));
//                                    seekBar.setProgress(0);
//                                }
//
//                            }
//
//                            @Override
//                            public void onStopTrackingTouch(SeekBar seekBar) {
//
//
//                                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//                                    mediaPlayer.seekTo(seekBar.getProgress());
//                                }
//                            }
//                        });
                    } else {
                        contactViewHolder.doc_left.setVisibility(View.VISIBLE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        contactViewHolder.chat_right_msg_layout.setVisibility(View.GONE);
                        contactViewHolder.chat_left_msg_layout.setVisibility(View.GONE);
                        contactViewHolder.chat_left_Image.setVisibility(View.GONE);
                        contactViewHolder.chat_right_Image.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);
                        contactViewHolder.timestamp_doc_rec.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                        contactViewHolder.doc_name_rec.setText(chatList.getMessage());
                    }
                }

            } else if (chatList.getUser_me().equalsIgnoreCase("0")) {
                contactViewHolder.chat_right_msg_layout.setVisibility(View.GONE);
                contactViewHolder.chat_left_msg_layout.setVisibility(View.GONE);
                contactViewHolder.chat_left_Image.setVisibility(View.GONE);
                contactViewHolder.chat_right_Image.setVisibility(View.GONE);
                contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                contactViewHolder.chat_right_voice.setVisibility(View.GONE);
                contactViewHolder.doc_left.setVisibility(View.GONE);
                contactViewHolder.documet_right_ll.setVisibility(View.GONE);

                contactViewHolder.reply_l1.setVisibility(View.GONE);
                contactViewHolder.fd_text.setVisibility(View.GONE);
                if (chatList.getReply_id().length() > 0 && chatList.getForward_status().equalsIgnoreCase("0")) {
                    ArrayList<UserRepo> rep_msg = db_tables.getRepMessage(chatList.getReply_id());
                    contactViewHolder.reply_l1.setVisibility(View.VISIBLE);
                    contactViewHolder.mesg_reps_send.setText(Html.fromHtml(rep_msg.get(0).getMessage()));
                    contactViewHolder.txtMessage_send_reply.setText(chatList.getMessage());
                    contactViewHolder.user_nnmae_rep_send.setText(chatList.getSender_name());
                    contactViewHolder.fd_text.setVisibility(View.GONE);
                    contactViewHolder.chat_right_Image.setVisibility(View.GONE);
                    contactViewHolder.chat_left_Image.setVisibility(View.GONE);
                    contactViewHolder.chat_right_voice.setVisibility(View.GONE);
                    contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                    contactViewHolder.chat_right_msg_layout.setVisibility(View.GONE);
                    contactViewHolder.chat_left_msg_layout.setVisibility(View.GONE);
                    contactViewHolder.doc_left.setVisibility(View.GONE);
                    contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                    contactViewHolder.timestamp_send_reply.setText(getDate(Long.parseLong(chatList.getMessage_date())));

                } else if (chatList.getForward_status().equalsIgnoreCase("1")) {

                    String myType = Utils.getMimeType(chatList.getMessage());
                    contactViewHolder.reply_l1.setVisibility(View.GONE);
                    if (myType == null) {
                        contactViewHolder.chat_left_msg_layout.setVisibility(View.VISIBLE);
                        contactViewHolder.fd_text.setVisibility(View.VISIBLE);
                        contactViewHolder.chat_right_Image.setVisibility(View.GONE);
                        contactViewHolder.chat_right_msg_layout.setVisibility(View.GONE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);
                        contactViewHolder.chat_left_Image.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.doc_left.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        contactViewHolder.txtMessage_send.setText(Html.fromHtml(chatList.getMessage()));
                        contactViewHolder.timestamp_send.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                    } else if (myType.startsWith("image/")) {
                        contactViewHolder.chat_left_msg_layout.setVisibility(View.GONE);
                        contactViewHolder.chat_right_msg_layout.setVisibility(View.GONE);
                        contactViewHolder.fd_text.setVisibility(View.GONE);
                        contactViewHolder.chat_right_Image.setVisibility(View.VISIBLE);
                        contactViewHolder.chat_left_Image.setVisibility(View.GONE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.doc_left.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        contactViewHolder.fd_img.setVisibility(View.VISIBLE);
                        contactViewHolder.vi_play_r.setVisibility(View.GONE);
                        Glide.with(context).load(Paths.up_load + chatList.getMessage()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(contactViewHolder.txtMessage_send_img);
                        contactViewHolder.timestamp_send_img.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                    } else if (myType.startsWith("video/")) {
                        contactViewHolder.chat_left_msg_layout.setVisibility(View.GONE);
                        contactViewHolder.chat_right_msg_layout.setVisibility(View.GONE);
                        contactViewHolder.fd_text.setVisibility(View.GONE);
                        contactViewHolder.chat_right_Image.setVisibility(View.VISIBLE);
                        contactViewHolder.chat_left_Image.setVisibility(View.GONE);
                        contactViewHolder.fd_img.setVisibility(View.VISIBLE);
                        contactViewHolder.vi_play_r.setVisibility(View.VISIBLE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.doc_left.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        Glide.with(context).load(Paths.up_load + chatList.getMessage()).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(contactViewHolder.txtMessage_send_img);
                        contactViewHolder.timestamp_send_img.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                    } else if (myType.startsWith("audio/")) {
                        contactViewHolder.chat_left_msg_layout.setVisibility(View.GONE);
                        contactViewHolder.chat_right_msg_layout.setVisibility(View.GONE);
                        contactViewHolder.fd_text.setVisibility(View.GONE);
                        contactViewHolder.chat_right_voice.setVisibility(View.VISIBLE);
                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.fd_voice_rt.setVisibility(View.VISIBLE);
                        contactViewHolder.chat_right_Image.setVisibility(View.GONE);
                        contactViewHolder.chat_left_Image.setVisibility(View.GONE);
                        contactViewHolder.doc_left.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.GONE);
                        final String url_voice = Paths.up_load + chatList.getMessage();
                        contactViewHolder.timestamp_vc_send.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                        contactViewHolder.ply_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Uri uri = Uri.parse(url_voice);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                intent.setDataAndType(uri, "audio/*");
                                context.startActivity(intent);

                            }
                        });
//                        contactViewHolder.ply_btn.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                try {
//
//                                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//                                        clearMediaPlayer();
//                                        contactViewHolder.seekBar.setProgress(0);
//                                        wasPlaying = true;
//                                        contactViewHolder.ply_btn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.play_btn));
//                                    }
//
//
//                                    if (!wasPlaying) {
//
//                                        if (mediaPlayer == null) {
//                                            mediaPlayer = new MediaPlayer();
//                                        }
//
//                                        contactViewHolder.ply_btn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.pause_btn));
//                                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//                                        mediaPlayer.setDataSource(url_voice);
//
//                                        mediaPlayer.prepare();
//                                        mediaPlayer.setVolume(0.5f, 0.5f);
//                                        mediaPlayer.setLooping(false);
//                                        contactViewHolder.seekBar.setMax(mediaPlayer.getDuration());
//
//                                        mediaPlayer.start();
//                                        new Thread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                int currentPosition = mediaPlayer.getCurrentPosition();
//                                                int total = mediaPlayer.getDuration();
//
//
//                                                while (mediaPlayer != null && mediaPlayer.isPlaying() && currentPosition < total) {
//                                                    try {
//                                                        Thread.sleep(1000);
//                                                        currentPosition = mediaPlayer.getCurrentPosition();
//                                                    } catch (InterruptedException e) {
//                                                        return;
//                                                    } catch (Exception e) {
//                                                        return;
//                                                    }
//
//                                                    contactViewHolder.seekBar.setProgress(currentPosition);
//
//                                                }
//                                            }
//                                        }).start();
//
//                                    }
//
//                                    wasPlaying = false;
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//
//                                }
//                            }
//                        });
//
//
//                        contactViewHolder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                            @Override
//                            public void onStartTrackingTouch(SeekBar seekBar) {
//
//                                contactViewHolder.timer_t.setVisibility(View.VISIBLE);
//                            }
//
//                            @Override
//                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
//                                contactViewHolder.timer_t.setVisibility(View.VISIBLE);
//                                int x = (int) Math.ceil(progress / 1000f);
//
//                                if (x < 10)
//                                    contactViewHolder.timer_t.setText("0:0" + x);
//                                else
//                                    contactViewHolder.timer_t.setText("0:" + x);
//
//                                double percent = progress / (double) seekBar.getMax();
//                                int offset = seekBar.getThumbOffset();
//                                int seekWidth = seekBar.getWidth();
//                                int val = (int) Math.round(percent * (seekWidth - 2 * offset));
//                                int labelWidth = contactViewHolder.timer_t.getWidth();
//                                contactViewHolder.timer_t.setX(offset + seekBar.getX() + val
//                                        - Math.round(percent * offset)
//                                        - Math.round(percent * labelWidth / 2));
//
//                                if (progress > 0 && mediaPlayer != null && !mediaPlayer.isPlaying()) {
//                                    clearMediaPlayer();
//                                    contactViewHolder.ply_btn.setImageDrawable(ContextCompat.getDrawable(context, R.mipmap.play_btn));
//                                    seekBar.setProgress(0);
//                                }
//
//                            }
//
//                            @Override
//                            public void onStopTrackingTouch(SeekBar seekBar) {
//
//
//                                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//                                    mediaPlayer.seekTo(seekBar.getProgress());
//                                }
//                            }
//                        });
                    } else {
                        contactViewHolder.chat_left_msg_layout.setVisibility(View.GONE);
                        contactViewHolder.chat_right_msg_layout.setVisibility(View.GONE);
                        contactViewHolder.fd_text.setVisibility(View.GONE);
                        contactViewHolder.chat_right_voice.setVisibility(View.GONE);
                        contactViewHolder.chat_left_voice.setVisibility(View.GONE);
                        contactViewHolder.chat_right_Image.setVisibility(View.GONE);
                        contactViewHolder.chat_left_Image.setVisibility(View.GONE);
                        contactViewHolder.documet_right_ll.setVisibility(View.VISIBLE);
                        contactViewHolder.doc_left.setVisibility(View.GONE);
                        contactViewHolder.fd_doc_lt.setVisibility(View.VISIBLE);
                        contactViewHolder.doc_name.setText(Html.fromHtml(chatList.getMessage()));
                        contactViewHolder.timestamp_doc_sent.setText(getDate(Long.parseLong(chatList.getMessage_date())));
                    }

                }

            }
            applyClickEvents(contactViewHolder, matchesList, position);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void applyClickEvents(final ContactViewHolder holder, final List<MessageObject> matchesList, final int position) {


        holder.chat_right_msg_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(matchesList, position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
        holder.doc_left.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(matchesList, position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
        holder.chat_left_Image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(matchesList, position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
        holder.chat_left_voice.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onRowLongClicked(matchesList, position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                return true;
            }
        });
        holder.chat_left_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageImages(matchesList, position);
            }
        });
        holder.chat_right_Image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageImages(matchesList, position);
            }
        });
        holder.doc_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageImages(matchesList, position);
            }
        });
        holder.documet_right_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageImages(matchesList, position);
            }
        });

    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.chat_app_item_view, parent, false);
        ContactViewHolder myHoder = new ContactViewHolder(view);
        return myHoder;
    }


    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        protected TextView txtMessage_send;
        protected TextView fd_text;
        protected TextView fd_text_rc;
        protected TextView timestamp_send_reply;
        protected TextView user_nnmae_rep_send;
        protected TextView txtMessage_send_reply;
        protected TextView mesg_reps_send;
        protected TextView txtMessage_rec;
        protected TextView timestamp_rec;
        protected TextView timestamp_send;
        protected TextView my_header_date;
        protected TextView timestamp_rec_img;
        protected TextView doc_name_rec;
        protected TextView timestamp_doc_rec;
        protected LinearLayout vi_play;
        protected ImageView leftImgeMessage_rec;
        public LinearLayout d_header;
        public LinearLayout chat_left_msg_layout;
        public LinearLayout chat_right_msg_layout;
        public LinearLayout chat_left_Image;
        public LinearLayout chat_right_Image;
        public LinearLayout reply_l1;
        public LinearLayout chat_left_voice;
        public LinearLayout doc_left;
        public LinearLayout vi_play_r;
        public LinearLayout chat_right_voice;
        public LinearLayout documet_right_ll;
        ImageView ply_btn;
        ImageView ply_btn_rec;
        ImageView txtMessage_send_img;
        TextView timer_t;
        TextView voice_time_rec;
        TextView timestamp_vc_send;

        TextView fd_doc_rt;
        TextView fd_doc_lt;
        TextView fd_img_rt;
        TextView fd_voice_lt;
        TextView fd_img;
        TextView fd_voice_rt;
        TextView timestamp_send_img;
        TextView timestamp_doc_sent;
        TextView doc_name;
        TextView tk_name;
        TextView tk_name_img;


        TextView timer_t_rec;
        SeekBar seekBar;
        SeekBar seekBar_rec;

        public ContactViewHolder(View v) {
            super(v);

            txtMessage_send = (TextView) v.findViewById(R.id.txtMessage_send);
            fd_text = (TextView) v.findViewById(R.id.fd_text);
            fd_text_rc = (TextView) v.findViewById(R.id.fd_text_rc);
            timestamp_send_reply = (TextView) v.findViewById(R.id.timestamp_send_reply);
            user_nnmae_rep_send = (TextView) v.findViewById(R.id.user_nnmae_rep_send);
            txtMessage_send_reply = (TextView) v.findViewById(R.id.txtMessage_send_reply);
            mesg_reps_send = (TextView) v.findViewById(R.id.mesg_reps_send);
            txtMessage_rec = (TextView) v.findViewById(R.id.txtMessage_rec);
            timestamp_rec = (TextView) v.findViewById(R.id.timestamp_rec);
            my_header_date = (TextView) v.findViewById(R.id.date_header);
            timestamp_send = (TextView) v.findViewById(R.id.timestamp_send);
            timestamp_rec_img = (TextView) v.findViewById(R.id.timestamp_rec_img);
            timestamp_send_img = (TextView) v.findViewById(R.id.timestamp_send_img);
            timestamp_doc_rec = (TextView) v.findViewById(R.id.timestamp_doc_rec);
            timestamp_vc_send = (TextView) v.findViewById(R.id.timestamp_vc_send);
            doc_name_rec = (TextView) v.findViewById(R.id.doc_name_rec);
            fd_doc_rt = (TextView) v.findViewById(R.id.fd_doc_rt);
            fd_img_rt = (TextView) v.findViewById(R.id.fd_img_rt);
            fd_voice_lt = (TextView) v.findViewById(R.id.fd_voice_lt);
            fd_doc_lt = (TextView) v.findViewById(R.id.fd_doc_lt);
            vi_play = v.findViewById(R.id.vi_play);
            doc_left = v.findViewById(R.id.documet_left_ll);
            fd_img = v.findViewById(R.id.fd_img);
            documet_right_ll = v.findViewById(R.id.documet_right_ll);
            txtMessage_send_img = v.findViewById(R.id.txtMessage_send_img);
            chat_right_Image = v.findViewById(R.id.chat_right_Image);
            chat_left_voice = v.findViewById(R.id.chat_left_voice);
            chat_right_voice = v.findViewById(R.id.chat_right_voice);
            leftImgeMessage_rec = (ImageView) v.findViewById(R.id.leftImgeMessage_rec);
            d_header = v.findViewById(R.id.d_header);
            chat_left_msg_layout = v.findViewById(R.id.chat_left_msg_layout);
            chat_right_msg_layout = v.findViewById(R.id.chat_right_msg_layout);
            chat_left_Image = v.findViewById(R.id.chat_left_Image);
            reply_l1 = itemView.findViewById(R.id.reply_l1);

            ply_btn = v.findViewById(R.id.ply_btn);
            vi_play_r = v.findViewById(R.id.vi_play_r);
            ply_btn_rec = v.findViewById(R.id.ply_btn_rec);
            timer_t = v.findViewById(R.id.timer_t);
            timestamp_doc_sent = v.findViewById(R.id.timestamp_doc_sent);
            timer_t_rec = v.findViewById(R.id.timer_t_rec);
            voice_time_rec = v.findViewById(R.id.voice_time_rec);
            fd_voice_rt = v.findViewById(R.id.fd_voice_rt);
            seekBar = v.findViewById(R.id.seekbar);
            seekBar_rec = v.findViewById(R.id.seekbar_rec);
            doc_name = v.findViewById(R.id.doc_name);
            tk_name = v.findViewById(R.id.tk_name);
            tk_name_img = v.findViewById(R.id.tk_name_img);
        }

        @Override
        public boolean onLongClick(View view) {
            listener.onRowLongClicked(matchesList, getAdapterPosition());
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }

    public interface ChatAdapterListener {
        void onRowLongClicked(List<MessageObject> matchesList, int position);

        void onMessageImages(List<MessageObject> matchesList, int position);

    }

    private String getDate(long timeStamp) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "date";
        }
    }


    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    private void resetCurrentIndex() {
        currentSelectedIndex = -1;
    }

    public void resetAnimationIndex() {
        reverseAllAnimations = false;
        animationItemsIndex.clear();
    }

    private void clearMediaPlayer() {
        //mediaPlayer.pause();
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }
}
