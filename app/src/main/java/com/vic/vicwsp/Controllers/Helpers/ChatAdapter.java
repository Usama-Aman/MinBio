package com.vic.vicwsp.Controllers.Helpers;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.vic.vicwsp.Models.Response.Chat.Datum;
import com.vic.vicwsp.R;
import com.vic.vicwsp.Utils.Constants;
import com.vic.vicwsp.Utils.SharedPreference;

import java.text.DecimalFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import ozaydin.serkan.com.image_zoom_view.ImageViewZoom;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Datum> datumArrayList;
    private Context context;

    private final int ITEM_PROGRESS = -1;
    private final int MESSAGE_RIGHT = 0;
    private final int MESSAGE_LEFT = 1;
    private final int IMAGE_RIGHT = 2;
    private final int IMAGE_LEFT = 3;
    private final int AUDIO_RIGHT = 4;
    private final int AUDIO_LEFT = 5;

    private TextMessageLeft textMessageLeft;
    private TextMessageRight textMessageRight;
    private ImageMessageLeft imageMessageLeft;
    private ImageMessageRight imageMessageRight;
    private AudioMessageLeft audioMessageLeft;
    private AudioMessageRight audioMessageRight;

    private DecimalFormat df;
    private Fragment fragment;
    private MediaPlayer mediaPlayer;
    private int playingPosition = -1;

    public ChatAdapter(Context context, ArrayList<Datum> datumArrayList) {
        this.datumArrayList = datumArrayList;
        this.context = context;
        df = new DecimalFormat("#.#");
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        try {
            switch (viewType) {
                case MESSAGE_RIGHT:
                    return new TextMessageRight(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_right, parent, false));
                case MESSAGE_LEFT:
                    return new TextMessageLeft(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_left, parent, false));
                case IMAGE_RIGHT:
                    return new ImageMessageRight(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_image_right, parent, false));
                case IMAGE_LEFT:
                    return new ImageMessageLeft(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_image_left, parent, false));
                case AUDIO_RIGHT:
                    return new AudioMessageRight(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_audio_right, parent, false));
                case AUDIO_LEFT:
                    return new AudioMessageLeft(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_audio_left, parent, false));
                case ITEM_PROGRESS:
                    return new Progress(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loader_pagination_products, parent, false));
                default:
                    return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        try {
            if (holder.getItemViewType() == MESSAGE_RIGHT) {
                textMessageRight = (TextMessageRight) holder;
                textMessageRight.bind(position);
            } else if (holder.getItemViewType() == MESSAGE_LEFT) {
                textMessageLeft = (TextMessageLeft) holder;
                textMessageLeft.bind(position);
            } else if (holder.getItemViewType() == IMAGE_RIGHT) {
                imageMessageRight = (ImageMessageRight) holder;
                imageMessageRight.bind(position);
            } else if (holder.getItemViewType() == IMAGE_LEFT) {
                imageMessageLeft = (ImageMessageLeft) holder;
                imageMessageLeft.bind(position);
            } else if (holder.getItemViewType() == AUDIO_RIGHT) {
                audioMessageRight = (AudioMessageRight) holder;
                audioMessageRight.bind(position);
            } else if (holder.getItemViewType() == AUDIO_LEFT) {
                audioMessageLeft = (AudioMessageLeft) holder;
                audioMessageLeft.bind(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return datumArrayList == null ? 0 : datumArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        try {

            if (datumArrayList.get(position) == null)
                return ITEM_PROGRESS;
            else {
                if (String.valueOf(datumArrayList.get(position).getSenderId()).equals(SharedPreference.getSimpleString(context, Constants.userId)))  // For Right Messages
                {
                    if ("text".equals(datumArrayList.get(position).getType())) {
                        return MESSAGE_RIGHT;
                    } else if ("image".equals(datumArrayList.get(position).getType())) {
                        return IMAGE_RIGHT;
                    } else if ("audio".equals(datumArrayList.get(position).getType())) {
                        return AUDIO_RIGHT;
                    }

                } else //For Left Messages
                {
                    if ("text".equals(datumArrayList.get(position).getType())) {
                        return MESSAGE_LEFT;
                    } else if ("image".equals(datumArrayList.get(position).getType())) {
                        return IMAGE_LEFT;
                    } else if ("audio".equals(datumArrayList.get(position).getType())) {
                        return AUDIO_LEFT;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -10;
    }

    public class TextMessageLeft extends RecyclerView.ViewHolder {

        private CircleImageView userImage;
        private TextView messageTime;
        private TextView textMessage;


        public TextMessageLeft(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.userImage);
            messageTime = itemView.findViewById(R.id.messageTime);
            textMessage = itemView.findViewById(R.id.textMessage);
        }

        public void bind(int position) {
            try {
                messageTime.setText(datumArrayList.get(position).getDate());
                textMessage.setText(datumArrayList.get(position).getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class TextMessageRight extends RecyclerView.ViewHolder {

        private CircleImageView userImage;
        private TextView messageTime;
        private TextView textMessage;
        private ImageView tick;


        public TextMessageRight(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            messageTime = itemView.findViewById(R.id.messageTime);
            textMessage = itemView.findViewById(R.id.textMessage);
            tick = itemView.findViewById(R.id.messageTick);
        }

        public void bind(int position) {
            try {
                messageTime.setText(datumArrayList.get(position).getDate());
                textMessage.setText(datumArrayList.get(position).getMessage());

                if (datumArrayList.get(position).getIsDelivered() == 1) {
                    tick.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_received_tick));
                } else if (datumArrayList.get(position).getIsSeen() == 1)
                    tick.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_read_tick));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class ImageMessageLeft extends RecyclerView.ViewHolder {

        private CircleImageView userImage;
        private TextView messageTime;
        private ImageViewZoom imageMessage;
        private ConstraintLayout imageMessageLayout;

        public ImageMessageLeft(@NonNull View itemView) {
            super(itemView);

            userImage = itemView.findViewById(R.id.userImage);
            messageTime = itemView.findViewById(R.id.messageTime);
            imageMessage = itemView.findViewById(R.id.imageMessage);
            imageMessageLayout = itemView.findViewById(R.id.imageMessageLayout);
        }

        public void bind(int position) {
            try {
                Glide.with(context).load(datumArrayList.get(position).getFilePath()).into(imageMessage);
                messageTime.setText(datumArrayList.get(position).getDate());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class ImageMessageRight extends RecyclerView.ViewHolder {

        private CircleImageView userImage;
        private TextView messageTime;
        private ImageViewZoom imageMessage;
        private ConstraintLayout imageMessageLayout;
        private ImageView tick;

        public ImageMessageRight(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            messageTime = itemView.findViewById(R.id.messageTime);
            imageMessage = itemView.findViewById(R.id.imageMessage);
            imageMessageLayout = itemView.findViewById(R.id.imageMessageLayout);
            tick = itemView.findViewById(R.id.messageTick);
        }

        public void bind(int position) {

            try {
                Glide.with(context).load(datumArrayList.get(position).getFilePath()).into(imageMessage);
                messageTime.setText(datumArrayList.get(position).getDate());

                if (datumArrayList.get(position).getIsDelivered() == 1) {
                    tick.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_received_tick));
                } else if (datumArrayList.get(position).getIsSeen() == 1)
                    tick.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_read_tick));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class AudioMessageLeft extends RecyclerView.ViewHolder {

        private CircleImageView userImage;
        private TextView messageTime, audioDuration;
        private AppCompatSeekBar seekBarHolder;
        private MaterialRippleLayout playAudio, pauseAudio;
        private int duration = 0;
        private ProgressBar progressBar;
        private ImageView btnPlayAudio;

        private boolean isDownloaded = false;

        public AudioMessageLeft(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            messageTime = itemView.findViewById(R.id.messageTime);
            seekBarHolder = itemView.findViewById(R.id.seekbar);
            playAudio = itemView.findViewById(R.id.btnPlayAudio);
            audioDuration = itemView.findViewById(R.id.audioDuration);
            progressBar = itemView.findViewById(R.id.progressBar);
            btnPlayAudio = itemView.findViewById(R.id.ivBtnPlayAudio);
        }

        public void bind(int position) {
            try {
                messageTime.setText(datumArrayList.get(position).getDate());
                audioDuration.setText(datumArrayList.get(position).getDuration());

                if (datumArrayList.get(position).isAudioPlaying()) {
                    btnPlayAudio.setImageDrawable(context.getResources().getDrawable(R.drawable.pause));
                } else {
                    btnPlayAudio.setImageDrawable(context.getResources().getDrawable(R.drawable.play));
                }

                seekBarHolder.setProgress(datumArrayList.get(position).getAudioProgressBar());

                playAudio.setOnClickListener(view -> {
                    try {
                        datumArrayList.get(position).setAudioPlaying(true);

                        if (playingPosition == getAdapterPosition()) {
                            btnPlayAudio.setImageDrawable(context.getResources().getDrawable(R.drawable.play));
                            datumArrayList.get(position).setAudioProgressBar(0);
                            datumArrayList.get(position).setAudioPlaying(false);
                            mediaPlayer.stop();
//                        notifyItemChanged(position);

                            playingPosition = -1;
                        } else {

                            if (mediaPlayer != null)
                                if (mediaPlayer.isPlaying()) {
                                    datumArrayList.get(playingPosition).setAudioProgressBar(0);
                                    datumArrayList.get(playingPosition).setAudioPlaying(false);
                                    seekBarHolder.setProgress(0);
                                    mediaPlayer.stop();

                                    notifyItemChanged(playingPosition);
                                }

                            btnPlayAudio.setImageDrawable(context.getResources().getDrawable(R.drawable.pause));

                            try {
                                mediaPlayer = new MediaPlayer();
                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                mediaPlayer.setDataSource(datumArrayList.get(position).getFilePath());
                                mediaPlayer.prepareAsync();
                                final Handler[] mHandler = new Handler[1];

                                mediaPlayer.setOnPreparedListener(mp -> {
                                    btnPlayAudio.setImageDrawable(context.getResources().getDrawable(R.drawable.pause));
                                    playingPosition = getAdapterPosition();
                                    mediaPlayer.start();
                                    seekBarHolder.setMax(mediaPlayer.getDuration());
//                                    seekBarHolder.setMax((int) Common.round((double) (mediaPlayer.getDuration() / 1000), 2));

                                    mHandler[0] = new Handler();
                                    ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                                    int mCurrentPosition = mediaPlayer.getCurrentPosition();
                                                    if (playingPosition == getAdapterPosition()) {
                                                        seekBarHolder.setProgress(mCurrentPosition);
                                                        audioDuration.setText(milliSecondsToTimer((long) mCurrentPosition));
                                                    }
                                                }

                                                mHandler[0].postDelayed(this, 100);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });


                                    seekBarHolder.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                        @Override
                                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                                            if (mediaPlayer != null && fromUser) {
                                                mediaPlayer.seekTo(progress * 1000);
                                            }
                                        }

                                        @Override
                                        public void onStartTrackingTouch(SeekBar seekBar) {

                                        }

                                        @Override
                                        public void onStopTrackingTouch(SeekBar seekBar) {

                                        }
                                    });

                                });

                                mediaPlayer.setOnCompletionListener(mp -> {
                                    audioDuration.setText(datumArrayList.get(position).getDuration());
                                    mediaPlayer.reset();
                                    btnPlayAudio.setImageDrawable(context.getResources().getDrawable(R.drawable.play));
                                    seekBarHolder.setProgress(0);
                                    datumArrayList.get(position).setAudioPlaying(false);
                                    datumArrayList.get(position).setAudioProgressBar(0);
                                    notifyItemChanged(position);
                                    mHandler[0].removeCallbacks(null);

                                    playingPosition = -1;
                                });


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class AudioMessageRight extends RecyclerView.ViewHolder {

        private CircleImageView userImage;
        private TextView messageTime, audioDuration;
        private AppCompatSeekBar seekBarHolder;
        private MaterialRippleLayout playAudio, pauseAudio;
        private double duration;
        private ImageView tick, btnPlayAudio;
        private ProgressBar progressBar;

        private boolean isDownloaded = false;

        public AudioMessageRight(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            messageTime = itemView.findViewById(R.id.messageTime);
            seekBarHolder = itemView.findViewById(R.id.seekbar);
            playAudio = itemView.findViewById(R.id.btnPlayAudio);
            audioDuration = itemView.findViewById(R.id.audioDuration);
            tick = itemView.findViewById(R.id.messageTick);
            progressBar = itemView.findViewById(R.id.progressBar);
            btnPlayAudio = itemView.findViewById(R.id.ivBtnPlayAudio);
        }

        public void bind(int position) {
            try {
                messageTime.setText(datumArrayList.get(position).getDate());
                audioDuration.setText(datumArrayList.get(position).getDuration());

                if (datumArrayList.get(position).isAudioPlaying()) {
                    btnPlayAudio.setImageDrawable(context.getResources().getDrawable(R.drawable.pause));
                } else {
                    btnPlayAudio.setImageDrawable(context.getResources().getDrawable(R.drawable.play));
                }

                seekBarHolder.setProgress(datumArrayList.get(position).getAudioProgressBar());

                playAudio.setOnClickListener(view -> {

                    try {
                        datumArrayList.get(position).setAudioPlaying(true);

                        if (playingPosition == getAdapterPosition()) {
                            btnPlayAudio.setImageDrawable(context.getResources().getDrawable(R.drawable.play));
                            datumArrayList.get(position).setAudioProgressBar(0);
                            datumArrayList.get(position).setAudioPlaying(false);
                            mediaPlayer.stop();
                            playingPosition = -1;
                        } else {

                            if (mediaPlayer != null)
                                if (mediaPlayer.isPlaying()) {
                                    datumArrayList.get(playingPosition).setAudioProgressBar(0);
                                    datumArrayList.get(playingPosition).setAudioPlaying(false);
                                    seekBarHolder.setProgress(0);
                                    mediaPlayer.stop();

                                    notifyItemChanged(playingPosition);
                                }

                            btnPlayAudio.setImageDrawable(context.getResources().getDrawable(R.drawable.pause));

                            try {
                                mediaPlayer = new MediaPlayer();
                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                mediaPlayer.setDataSource(datumArrayList.get(position).getFilePath());
                                mediaPlayer.prepareAsync();
                                final Handler[] mHandler = new Handler[1];
                                mediaPlayer.setOnPreparedListener(mp -> {
                                    btnPlayAudio.setImageDrawable(context.getResources().getDrawable(R.drawable.pause));
                                    playingPosition = getAdapterPosition();
                                    mediaPlayer.start();
                                    seekBarHolder.setMax(mediaPlayer.getDuration());
                                    mHandler[0] = new Handler();
                                    ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                                                    int mCurrentPosition = mediaPlayer.getCurrentPosition();
                                                    if (playingPosition == getAdapterPosition()) {
                                                        seekBarHolder.setProgress(mCurrentPosition);
                                                        audioDuration.setText(milliSecondsToTimer((long) mCurrentPosition));
                                                    }
                                                }

                                                mHandler[0].postDelayed(this, 100);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                });

                                mediaPlayer.setOnCompletionListener(mp -> {
                                    audioDuration.setText(datumArrayList.get(position).getDuration());
                                    mediaPlayer.reset();
                                    btnPlayAudio.setImageDrawable(context.getResources().getDrawable(R.drawable.play));
                                    seekBarHolder.setProgress(0);
                                    datumArrayList.get(position).setAudioPlaying(false);
                                    datumArrayList.get(position).setAudioProgressBar(0);
                                    notifyItemChanged(position);
                                    mHandler[0].removeCallbacks(null);

                                    playingPosition = -1;
                                });
                                seekBarHolder.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                    @Override
                                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                                        if (mediaPlayer != null && fromUser) {
                                            mediaPlayer.seekTo(progress * 1000);
                                        }
                                    }

                                    @Override
                                    public void onStartTrackingTouch(SeekBar seekBar) {

                                    }

                                    @Override
                                    public void onStopTrackingTouch(SeekBar seekBar) {

                                    }
                                });

                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";
        String minutesString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        if (minutes < 10) {
            minutesString = "0" + minutes;
        } else {
            minutesString = "" + minutesString;
        }
        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutesString + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    public class Progress extends RecyclerView.ViewHolder {

        public ProgressBar progressBar;

        public Progress(View v) {
            super(v);
            progressBar = itemView.findViewById(R.id.progressBarPagination);
        }
    }

    public void stopMediaPLayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();  //Stops playback after playback has been stopped or paused
            mediaPlayer.release(); //Releases resources associated with this MediaPlayer object
            mediaPlayer = null;
        }
    }

}
