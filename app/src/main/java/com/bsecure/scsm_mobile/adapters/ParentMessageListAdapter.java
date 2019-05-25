package com.bsecure.scsm_mobile.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
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

public class ParentMessageListAdapter extends RecyclerView.Adapter<ParentMessageListAdapter.ContactViewHolder> {

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

    public ParentMessageListAdapter(List<MessageObject> list, Context context, ChatAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.matchesList = list;
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
            String chk_v = chatList.getUser_me();

            contactViewHolder.rightMsgLayout.setVisibility(View.VISIBLE);
            contactViewHolder.tk_name.setText(chatList.getSender_name());
            contactViewHolder.tk_name.setVisibility(View.VISIBLE);
            contactViewHolder.vi_play.setVisibility(View.GONE);
            contactViewHolder.leftMsgLayout.setVisibility(View.GONE);
            contactViewHolder.txtMessage_rec.setText(Html.fromHtml(chatList.getMessage()));
            contactViewHolder.timestamp_rec.setText(getDate(Long.parseLong(chatList.getMessage_date())));
            contactViewHolder.leftImgLayout.setVisibility(View.GONE);
            contactViewHolder.rightImgLayout.setVisibility(View.GONE);
            contactViewHolder.chat_right_voice.setVisibility(View.GONE);
            contactViewHolder.chat_left_voice.setVisibility(View.GONE);
            contactViewHolder.documet_left_ll.setVisibility(View.GONE);
            contactViewHolder.documet_right_ll.setVisibility(View.GONE);
            contactViewHolder.reply_l2.setVisibility(View.GONE);
            contactViewHolder.reply_l1.setVisibility(View.GONE);
            contactViewHolder.reply_l1_doc.setVisibility(View.GONE);
            contactViewHolder.reply_l2_doc.setVisibility(View.GONE);
            contactViewHolder.reply_l1_img.setVisibility(View.GONE);
            contactViewHolder.reply_l2_img.setVisibility(View.GONE);
            boolean value = selectedItems.get(position);
            contactViewHolder.itemView.setActivated(selectedItems.get(position, false));
           // applyClickEvents(contactViewHolder, matchesList, position, value);
            //applyIconAnimation(contactViewHolder, matchesList, position);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.chat_app_item_view2, parent, false);
        ContactViewHolder myHoder = new ContactViewHolder(view);
        return myHoder;
    }


    public class ContactViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        protected TextView txtMessage_send;
        protected TextView txtMessage_rec;
        protected TextView timestamp_send;
        protected TextView timestamp_rec;
        protected TextView timestamp_send_img;
        protected TextView timestamp_rec_img;
        protected TextView timestamp_vc_send;
        protected TextView voice_time_rec;
        protected TextView doc_name;
        protected TextView doc_name_rec;
        protected TextView timestamp_doc_sent;
        protected TextView timestamp_doc_rec;
        protected TextView mesg_reps_send;
        protected TextView user_nnmae_rep_send;
        protected TextView txtMessage_send_reply;
        protected TextView timestamp_send_reply;
        protected TextView mesg_reps_reply;
        protected TextView txtMessage_send_rec_rep;
        protected TextView timestamp_send_rp_t;
        protected TextView r_img_rep_send;
        protected TextView reps_send_img_txt;
        protected TextView timerp_send_reply;
        protected TextView r_rp_t;
        protected TextView mesg_reps_reply_1;
        protected TextView doc_send_name;
        protected TextView doc_rep_send;
        protected TextView doc_name_rep_send;
        protected TextView timestamp_doc_sent_rep;
        protected TextView r_img_rep_send_rr;
        protected TextView reps_send_img_txt_rr;
        protected TextView doc_name_rec_rr;
        protected TextView timestamp_doc_rec_rr;
        protected TextView fwText;
        protected TextView fd_text_rc;
        protected TextView fd_img;
        protected TextView fd_img_rt;
        protected TextView fd_voice_rt;
        protected TextView fd_voice_lt;
        protected TextView fd_doc_lt;
        protected TextView fd_doc_rt;
        protected TextView vo_send_name;
        protected TextView vo_rep_send;
        protected TextView voic_reply_1;
        protected TextView my_header_date;


        protected ImageView msg_viewd;
        protected ImageView msg_sent;
        protected ImageView msg_sent_img;
        protected ImageView txtMessage_send_img;
        protected ImageView leftImgeMessage_rec;
        protected ImageView msg_sent_voice;
        protected ImageView doc_img_rec;
        protected ImageView doc_img;
        protected ImageView doc_msg_sent;
        protected ImageView msg_sent_reply;
        protected ImageView rec_rp_type;
        protected ImageView rep_send_v;
        protected ImageView rec_img_pic;
        protected ImageView rep_send_img_d;
        protected ImageView send_repl_img;
        protected ImageView re_sent_reply;
        protected ImageView r_img_rec_rep;
        protected ImageView rep_rec_img_d;
        protected ImageView doc_img_rep;
        protected ImageView doc_msg_sent_rep;
        protected ImageView doc_img_rec_rr;
        protected ImageView vo_send_doc_d;
        protected ImageView voice_img_dp;


        protected LinearLayout leftMsgLayout;
        protected LinearLayout rightMsgLayout;
        protected LinearLayout leftImgLayout;
        protected LinearLayout rightImgLayout;
        protected LinearLayout chat_right_voice;
        protected LinearLayout chat_left_voice;
        protected LinearLayout documet_right_ll;
        protected LinearLayout documet_left_ll;
        protected LinearLayout reply_l1_doc;
        protected LinearLayout reply_l2_doc;

        public LinearLayout messageContainer;
        public LinearLayout reply_l1;
        public LinearLayout reply_l2;
        public LinearLayout reply_l1_img;
        public LinearLayout reply_l2_img;
        public LinearLayout vi_play;
        public LinearLayout vi_play_l;
        public LinearLayout vi_play_r;
        public LinearLayout vo_reply;
        public LinearLayout vo_sent;
        public LinearLayout d_header;


        ImageView ply_btn;
        ImageView ply_btn_rec;
        TextView timer_t;
        TextView tk_name;

        TextView timer_t_rec;
        SeekBar seekBar;
        SeekBar seekBar_rec;

        public ContactViewHolder(View v) {
            super(v);
            txtMessage_send = (TextView) v.findViewById(R.id.txtMessage_send);
            txtMessage_rec = (TextView) v.findViewById(R.id.txtMessage_rec);
            timestamp_rec_img = (TextView) v.findViewById(R.id.timestamp_rec_img);
            timestamp_send = (TextView) v.findViewById(R.id.timestamp_send);
            timestamp_rec = (TextView) v.findViewById(R.id.timestamp_rec);
            timestamp_send_img = (TextView) v.findViewById(R.id.timestamp_send_img);
            timestamp_vc_send = (TextView) v.findViewById(R.id.timestamp_vc_send);
            voice_time_rec = (TextView) v.findViewById(R.id.voice_time_rec);
            doc_name = (TextView) v.findViewById(R.id.doc_name);
            doc_name_rec = (TextView) v.findViewById(R.id.doc_name_rec);
            timestamp_doc_sent = (TextView) v.findViewById(R.id.timestamp_doc_sent);
            timestamp_doc_rec = (TextView) v.findViewById(R.id.timestamp_doc_rec);
            mesg_reps_reply = (TextView) v.findViewById(R.id.mesg_reps_reply);
            txtMessage_send_rec_rep = (TextView) v.findViewById(R.id.txtMessage_send_rec_rep);
            timestamp_send_rp_t = (TextView) v.findViewById(R.id.timestamp_send_rp_t);
            r_img_rep_send = (TextView) v.findViewById(R.id.r_img_rep_send);
            reps_send_img_txt = (TextView) v.findViewById(R.id.reps_send_img_txt);
            timerp_send_reply = (TextView) v.findViewById(R.id.timerp_send_reply);
            r_rp_t = (TextView) v.findViewById(R.id.r_rp_t);
            mesg_reps_reply_1 = (TextView) v.findViewById(R.id.mesg_reps_reply_1);
            doc_send_name = (TextView) v.findViewById(R.id.doc_send_name);
            doc_rep_send = (TextView) v.findViewById(R.id.doc_rep_send);
            doc_name_rep_send = (TextView) v.findViewById(R.id.doc_name_rep_send);
            timestamp_doc_sent_rep = (TextView) v.findViewById(R.id.timestamp_doc_sent_rep);
            r_img_rep_send_rr = (TextView) v.findViewById(R.id.r_img_rep_send_rr);
            reps_send_img_txt_rr = (TextView) v.findViewById(R.id.reps_send_img_txt_rr);
            doc_name_rec_rr = (TextView) v.findViewById(R.id.doc_name_rec_rr);
            timestamp_doc_rec_rr = (TextView) v.findViewById(R.id.timestamp_doc_rec_rr);
            fwText = (TextView) v.findViewById(R.id.fd_text);
            fd_text_rc = (TextView) v.findViewById(R.id.fd_text_rc);
            fd_img = (TextView) v.findViewById(R.id.fd_img);
            fd_img_rt = (TextView) v.findViewById(R.id.fd_img_rt);
            fd_voice_rt = (TextView) v.findViewById(R.id.fd_voice_rt);
            fd_voice_lt = (TextView) v.findViewById(R.id.fd_voice_lt);
            fd_doc_lt = (TextView) v.findViewById(R.id.fd_doc_lt);
            fd_doc_rt = (TextView) v.findViewById(R.id.fd_doc_rt);
            vo_send_name = (TextView) v.findViewById(R.id.vo_send_name);
            vo_rep_send = (TextView) v.findViewById(R.id.vo_rep_send);
            voic_reply_1 = (TextView) v.findViewById(R.id.voic_reply_1);
            tk_name = (TextView) v.findViewById(R.id.tk_name);

            my_header_date = (TextView) v.findViewById(R.id.date_header);

            rec_rp_type = (ImageView) v.findViewById(R.id.rec_rp_type);
            rep_send_v = (ImageView) v.findViewById(R.id.rep_send_v);
            rec_img_pic = (ImageView) v.findViewById(R.id.rec_img_pic);
            rep_send_img_d = (ImageView) v.findViewById(R.id.rep_send_img_d);
            send_repl_img = (ImageView) v.findViewById(R.id.send_repl_img);
            re_sent_reply = (ImageView) v.findViewById(R.id.re_sent_reply);
            r_img_rec_rep = (ImageView) v.findViewById(R.id.r_img_rec_rep);
            rep_rec_img_d = (ImageView) v.findViewById(R.id.rep_rec_img_d);
            doc_img_rep = (ImageView) v.findViewById(R.id.doc_img_rep);
            doc_msg_sent_rep = (ImageView) v.findViewById(R.id.doc_msg_sent_rep);
            doc_img_rec_rr = (ImageView) v.findViewById(R.id.doc_img_rec_rr);
            vo_send_doc_d = (ImageView) v.findViewById(R.id.vo_send_doc_d);
            voice_img_dp = (ImageView) v.findViewById(R.id.voice_img_dp);

            mesg_reps_send = (TextView) v.findViewById(R.id.mesg_reps_send);
            user_nnmae_rep_send = (TextView) v.findViewById(R.id.user_nnmae_rep_send);
            txtMessage_send_reply = (TextView) v.findViewById(R.id.txtMessage_send_reply);
            timestamp_send_reply = (TextView) v.findViewById(R.id.timestamp_send_reply);

            msg_sent = (ImageView) v.findViewById(R.id.msg_sent);
            msg_sent_img = (ImageView) v.findViewById(R.id.msg_sent_img);
            msg_sent_voice = (ImageView) v.findViewById(R.id.msg_sent_voice);
            txtMessage_send_img = (ImageView) v.findViewById(R.id.txtMessage_send_img);
            leftImgeMessage_rec = (ImageView) v.findViewById(R.id.leftImgeMessage_rec);
            doc_img_rec = (ImageView) v.findViewById(R.id.doc_img_rec);
            doc_img = (ImageView) v.findViewById(R.id.doc_img);
            doc_msg_sent = (ImageView) v.findViewById(R.id.doc_msg_sent);
            msg_sent_reply = (ImageView) v.findViewById(R.id.msg_sent_reply);


            leftMsgLayout = itemView.findViewById(R.id.chat_left_msg_layout);
            rightMsgLayout = itemView.findViewById(R.id.chat_right_msg_layout);
            leftImgLayout = itemView.findViewById(R.id.chat_left_Image);
            rightImgLayout = itemView.findViewById(R.id.chat_right_Image);
            chat_right_voice = itemView.findViewById(R.id.chat_right_voice);
            chat_left_voice = itemView.findViewById(R.id.chat_left_voice);
            documet_left_ll = itemView.findViewById(R.id.documet_left_ll);
            documet_right_ll = itemView.findViewById(R.id.documet_right_ll);
            reply_l1 = itemView.findViewById(R.id.reply_l1);
            reply_l2 = itemView.findViewById(R.id.reply_l2);
            reply_l1_img = itemView.findViewById(R.id.reply_l1_img);
            reply_l2_img = itemView.findViewById(R.id.reply_l2_img);
            reply_l1_doc = itemView.findViewById(R.id.reply_l1_doc);
            reply_l2_doc = itemView.findViewById(R.id.reply_l2_doc);

            d_header = v.findViewById(R.id.d_header);

            messageContainer = (LinearLayout) v.findViewById(R.id.messageContainer);
            vi_play = (LinearLayout) v.findViewById(R.id.vi_play);
            vi_play_l = (LinearLayout) v.findViewById(R.id.vi_play_l);
            vi_play_r = (LinearLayout) v.findViewById(R.id.vi_play_r);
            vo_reply = (LinearLayout) v.findViewById(R.id.vo_reply);
            vo_sent = (LinearLayout) v.findViewById(R.id.vo_sent);

            ply_btn = v.findViewById(R.id.ply_btn);
            ply_btn_rec = v.findViewById(R.id.ply_btn_rec);
            timer_t = v.findViewById(R.id.timer_t);
            timer_t_rec = v.findViewById(R.id.timer_t_rec);
            seekBar = v.findViewById(R.id.seekbar);
            seekBar_rec = v.findViewById(R.id.seekbar_rec);
            v.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            listener.onRowLongClicked(matchesList, getAdapterPosition(), false);
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
            return true;
        }
    }

    public interface ChatAdapterListener {
        void onMessageRowClicked(int position, String contactName, String contact_name, String pic_url, String contact_phone);

        void onMessageView(List<MessageObject> matchesList, int position);

        void onRowLongClicked(List<MessageObject> matchesList, int position, boolean value);
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

    private void applyClickEvents(final ContactViewHolder holder, final List<MessageObject> matchesList, final int position, final boolean value) {

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
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }
}
