package com.xiaopu.customer.adapter;

import android.content.Context;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaopu.customer.ApplicationXpClient;
import com.xiaopu.customer.R;
import com.xiaopu.customer.data.ChatMessage;
import com.xiaopu.customer.utils.ImageCheckoutUtil;
import com.xiaopu.customer.utils.security.Des;
import com.xiaopu.customer.view.BubbleImageView;

import java.util.List;

/**
 * Created by Administrator on 2018/1/2.
 */

public class MessageChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int SENDTXT = 1;

    private static final int SENDIMAGE = 2;

    private static final int FROMTXT = 3;

    private static final int FROMIMAGE = 4;

    private Context mContext;

    private List<ChatMessage> dataList;

    private LayoutInflater mInflater;

    private String userAvatar;

    private String doctorAvatar;


    private OnMessageItemClickListener itemClickListener;

    public MessageChatAdapter(Context mContext, List<ChatMessage> dataList, String userAvatar, String doctorAvatar) {
        this.mContext = mContext;
        this.dataList = dataList;
        this.userAvatar = userAvatar;
        this.doctorAvatar = doctorAvatar;
        mInflater = LayoutInflater.from(mContext);
    }

    /**
     * 这是一个添加一条数据并刷新界面的方法
     *
     * @param msg
     */
    public void addData(ChatMessage msg) {
        dataList.add(dataList.size(), msg);
        notifyItemInserted(dataList.size());
    }

    public void removeData(ChatMessage msg) {
        dataList.remove(msg);
//        notifyDataSetChanged();
    }

    public void changeData(int position, int state) {
        ChatMessage chatMessage = dataList.get(position);
        dataList.get(position).setSendState(state);
        notifyItemChanged(position);
    }


    public void setMessageItemClickListener(OnMessageItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage chatChatMessage = dataList.get(position);
        if (chatChatMessage.getMsgType() == 0) {
            if (chatChatMessage.getContentType() == 0) {
                return SENDTXT;
            } else if (chatChatMessage.getContentType() == 1) {
                return SENDIMAGE;
            }
        } else if (chatChatMessage.getMsgType() == 1) {
            if (chatChatMessage.getContentType() == 0) {
                return FROMTXT;
            } else if (chatChatMessage.getContentType() == 1) {
                return FROMIMAGE;
            }
        }
        return super.getItemViewType(position);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == SENDTXT) {
            view = mInflater.inflate(R.layout.chat_item_messageto, parent, false);
            return new SendTxtVIewHolder(view);
        } else if (viewType == SENDIMAGE) {
            view = mInflater.inflate(R.layout.chat_item_imageto, parent, false);
            return new SendImageVIewHolder(view);
        } else if (viewType == FROMTXT) {
            view = mInflater.inflate(R.layout.chat_item_messagefrom, parent, false);
            return new FromTxtVIewHolder(view);
        } else if (viewType == FROMIMAGE) {
            view = mInflater.inflate(R.layout.chat_item_imagefrom, parent, false);
            return new FromImageVIewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ChatMessage chatChatMessage = dataList.get(position);
        try {
            if (holder instanceof SendTxtVIewHolder) {
                ImageLoader.getInstance().displayImage(userAvatar, ((SendTxtVIewHolder) holder).iv_user_head, ApplicationXpClient.options);
                ((SendTxtVIewHolder) holder).tv_item_send_txt.setText(Des.decode(chatChatMessage.getContent()));
                if (chatChatMessage.getSendState() == 1) {
                    ((SendTxtVIewHolder) holder).pb_message.setVisibility(View.GONE);
                    ((SendTxtVIewHolder) holder).iv_message_error.setVisibility(View.GONE);
                } else if (chatChatMessage.getSendState() == 0) {
                    ((SendTxtVIewHolder) holder).pb_message.setVisibility(View.GONE);
                    ((SendTxtVIewHolder) holder).iv_message_error.setVisibility(View.VISIBLE);
                } else if (chatChatMessage.getSendState() == 2) {
                    ((SendTxtVIewHolder) holder).pb_message.setVisibility(View.VISIBLE);
                    ((SendTxtVIewHolder) holder).iv_message_error.setVisibility(View.GONE);
                }
                ((SendTxtVIewHolder) holder).iv_message_error.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.errorClick(dataList.get(position));
                    }
                });
                ((SendTxtVIewHolder) holder).iv_user_head.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.userAvatarClick();
                    }
                });

            } else if (holder instanceof SendImageVIewHolder) {
                ImageLoader.getInstance().displayImage(userAvatar, ((SendImageVIewHolder) holder).iv_user_head, ApplicationXpClient.options);
                int res;
                res = R.drawable.ease_chatto_bg_normal;
                if (chatChatMessage.getSendState() == 1) {
                    ((SendImageVIewHolder) holder).pb_message.setVisibility(View.GONE);
                    ((SendImageVIewHolder) holder).iv_message_error.setVisibility(View.GONE);
                } else if (chatChatMessage.getSendState() == 0) {
                    ((SendImageVIewHolder) holder).pb_message.setVisibility(View.GONE);
                    ((SendImageVIewHolder) holder).iv_message_error.setVisibility(View.VISIBLE);
                } else if (chatChatMessage.getSendState() == 2) {
                    ((SendImageVIewHolder) holder).pb_message.setVisibility(View.VISIBLE);
                    ((SendImageVIewHolder) holder).iv_message_error.setVisibility(View.GONE);
                }
                if (chatChatMessage.getImagePath() == null) {
                    if (chatChatMessage.getUri() != null) {
                        ((SendImageVIewHolder) holder).biv_chat_image.load(chatChatMessage.getUri(),
                                res, R.mipmap.default_chat_img);
                    }
                } else if (chatChatMessage.getUri() == null) {
                    if (chatChatMessage.getImagePath() != null) {
                        ((SendImageVIewHolder) holder).biv_chat_image.setLocalImageBitmap(ImageCheckoutUtil.getLoacalBitmap(chatChatMessage.getImagePath()),
                                res);
                    }
                }

                ((SendImageVIewHolder) holder).biv_chat_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.messageImageClick(dataList.get(position));
                    }
                });
                ((SendImageVIewHolder) holder).iv_user_head.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.userAvatarClick();
                    }
                });

                ((SendImageVIewHolder) holder).iv_message_error.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.errorClick(dataList.get(position));
                    }
                });

            } else if (holder instanceof FromTxtVIewHolder) {
                ImageLoader.getInstance().displayImage(doctorAvatar, ((FromTxtVIewHolder) holder).iv_doctor_avatar, ApplicationXpClient.options);
                ((FromTxtVIewHolder) holder).tv_item_from_txt.setText(Des.decode(chatChatMessage.getContent()));

                ((FromTxtVIewHolder) holder).iv_doctor_avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.doctorAvatarClick();
                    }
                });
            } else if (holder instanceof FromImageVIewHolder) {
                ImageLoader.getInstance().displayImage(doctorAvatar, ((FromImageVIewHolder) holder).iv_doctor_avatar, ApplicationXpClient.options);
                int res;
                res = R.drawable.ease_chatfrom_bg_normal;
                if (chatChatMessage.getImagePath() == null) {
                    if (chatChatMessage.getUri() != null) {
                        ((FromImageVIewHolder) holder).biv_chat_image.load(chatChatMessage.getUri(),
                                res, R.mipmap.default_chat_img);
                    }
                } else if (chatChatMessage.getUri() == null) {
                    if (chatChatMessage.getImagePath() != null) {
                        ((FromImageVIewHolder) holder).biv_chat_image.setLocalImageBitmap(ImageCheckoutUtil.getLoacalBitmap(chatChatMessage.getImagePath()),
                                res);
                    }
                }
                ((FromImageVIewHolder) holder).biv_chat_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.messageImageClick(dataList.get(position));
                    }
                });

                ((FromImageVIewHolder) holder).iv_doctor_avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemClickListener.doctorAvatarClick();
                    }
                });

            }
        } catch (Exception e) {

        }


    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    /**
     * 以下四个内部类为定义的不同的holder
     */
    class SendTxtVIewHolder extends RecyclerView.ViewHolder {
        private TextView tv_item_send_txt;

        private TextView tv_send_msg_date;

        private ImageView iv_user_head;

        private ProgressBar pb_message;

        private ImageView iv_message_error;

        public SendTxtVIewHolder(View view) {
            super(view);
            tv_item_send_txt = (TextView) view.findViewById(R.id.tv_chat_message);
            tv_send_msg_date = (TextView) view.findViewById(R.id.tv_chat_time);
            iv_user_head = (ImageView) view.findViewById(R.id.iv_user_head);
            pb_message = (ProgressBar) view.findViewById(R.id.pb_message);
            iv_message_error = (ImageView) view.findViewById(R.id.iv_message_error);
        }
    }

    class SendImageVIewHolder extends RecyclerView.ViewHolder {
        private BubbleImageView biv_chat_image;

        private ImageView iv_user_head;

        private TextView tv_send_msg_date;

        private ProgressBar pb_message;

        private ImageView iv_message_error;

        public SendImageVIewHolder(View view) {
            super(view);
            biv_chat_image = (BubbleImageView) view.findViewById(R.id.biv_chat_image);
            iv_user_head = (ImageView) view.findViewById(R.id.iv_user_head);
            tv_send_msg_date = (TextView) view.findViewById(R.id.tv_chat_time);
            pb_message = (ProgressBar) view.findViewById(R.id.pb_message);
            iv_message_error = (ImageView) view.findViewById(R.id.iv_message_error);
        }
    }

    class FromTxtVIewHolder extends RecyclerView.ViewHolder {
        private TextView tv_item_from_txt;
        private ImageView iv_doctor_avatar;
        private TextView tv_from_msg_date;

        public FromTxtVIewHolder(View view) {
            super(view);
            tv_item_from_txt = (TextView) view.findViewById(R.id.tv_chat_message);
            iv_doctor_avatar = (ImageView) view.findViewById(R.id.iv_doctor_head);
            tv_from_msg_date = (TextView) view.findViewById(R.id.tv_chat_time);
        }
    }

    class FromImageVIewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_doctor_avatar;

        private BubbleImageView biv_chat_image;

        private TextView tv_chat_time;

        public FromImageVIewHolder(View view) {
            super(view);
            iv_doctor_avatar = (ImageView) view.findViewById(R.id.iv_doctor_head);
            biv_chat_image = (BubbleImageView) view.findViewById(R.id.biv_chat_image);
            tv_chat_time = (TextView) view.findViewById(R.id.tv_chat_time);
        }
    }

    public interface OnMessageItemClickListener {
        void doctorAvatarClick();

        void userAvatarClick();

        void messageImageClick(ChatMessage chatMessage);

        void errorClick(ChatMessage chatMessage);
    }

}
