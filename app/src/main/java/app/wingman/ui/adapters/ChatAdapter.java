package app.wingman.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.quickblox.chat.QBChat;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBMessageListener;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatMessage;

import com.quickblox.content.QBContent;
import com.quickblox.core.QBCallbackImpl;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.result.Result;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.users.result.QBUserResult;
import com.squareup.picasso.Picasso;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import app.wingman.R;
import app.wingman.core.ChatService;
import app.wingman.ui.activities.ImageViewer;
import app.wingman.utils.PreferencesUtils;
import app.wingman.utils.TimeUtils;


public class ChatAdapter extends BaseAdapter implements QBMessageListener {

    public final List<QBChatMessage> chatMessages;
    private Activity context;

    @Override
    public void processMessage(QBChat qbChat, QBChatMessage qbChatMessage) {
        for (QBAttachment attachment : qbChatMessage.getAttachments()) {
            Integer fileId = Integer.parseInt(attachment.getId());

            // download a file
            QBContent.downloadFileTask(fileId, new QBEntityCallbackImpl<InputStream>() {
                @Override
                public void onSuccess(InputStream inputStream, Bundle params) {
                    // process file
                    Log.e("received msg", inputStream.toString());
                }

                @Override
                public void onError(List<String> errors) {
                    // errors
                }
            });
        }
    }

    @Override
    public void processError(QBChat qbChat, QBChatException e, QBChatMessage qbChatMessage) {

    }

    private enum ChatItemType {
        Message,
        Sticker
    }

    public ChatAdapter(Activity context, List<QBChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
    }

    @Override
    public int getCount() {
        if (chatMessages != null) {
            return chatMessages.size();
        } else {
            return 0;
        }
    }

    @Override
    public QBChatMessage getItem(int position) {
        if (chatMessages != null) {
            return chatMessages.get(position);
        } else {
            return null;
        }
    }

    @Override
    public int getViewTypeCount() {
        return ChatItemType.values().length;
    }
//
//    @Override
//    public int getItemViewType(int position) {
//        return StickersManager.isSticker(getItem(position).getBody())
//                ? ChatItemType.Sticker.ordinal()
//                : ChatItemType.Message.ordinal();
//    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final QBChatMessage chatMessage = getItem(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //  if (convertView == null)
        {
//            if (!getAttachUrlIfExists(chatMessage).equals("")) {
//                convertView = vi.inflate(R.layout.list_item_sticker, parent, false);
//            } else
            {
                convertView = vi.inflate(R.layout.list_item_message, parent, false);
            }
            holder = createViewHolder(convertView);
            convertView.setTag(holder);
        }

//        else {
//            holder = (ViewHolder) convertView.getTag();
//        }

        QBUser currentUser = ChatService.getInstance().getCurrentUser();
        boolean isOutgoing = chatMessage.getSenderId() == null || chatMessage.getSenderId().equals(currentUser.getId());
        setAlignment(holder, isOutgoing);
        try {
            if (!getAttachUrlIfExists(chatMessage).equals("")) {
                holder.txtMessage.setVisibility(View.GONE);

                if (!getAttachUrlIfExists(chatMessage).equals("")) {
                    Picasso.with(context)
                            .load(getAttachUrlIfExists(chatMessage))
                            .into(holder.stickerView);
                }

                holder.stickerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context,ImageViewer.class);
                        i.putExtra("IMG",getAttachUrlIfExists(chatMessage));
                        context.startActivity(i);
                    }
                });


            } else {
                holder.stickerView.setVisibility(View.GONE);
                holder.txtMessage.setText(chatMessage.getBody());

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (chatMessage.getSenderId() != null) {




            holder.txtInfo.setText(PreferencesUtils.getData(chatMessage.getSenderId()+"",context) + ": " + getTimeText(chatMessage));

        } else {

            holder.txtInfo.setText(getTimeText(chatMessage));
        }


        return convertView;
    }

    public static String getAttachUrlIfExists(QBChatMessage chatMessage) {
        String attachURL = "";
        Collection<QBAttachment> attachmentCollection = chatMessage.getAttachments();
        if (attachmentCollection != null && attachmentCollection.size() > 0) {
            attachURL = getAttachUrlFromMessage(attachmentCollection);
        }
        return attachURL;
    }

    public static String getAttachUrlFromMessage(Collection<QBAttachment> attachmentsCollection) {
        if (attachmentsCollection != null) {
            ArrayList<QBAttachment> attachmentsList = new ArrayList<QBAttachment>(attachmentsCollection);
            if (!attachmentsList.isEmpty()) {
                return attachmentsList.get(0).getUrl();
            }
        }
        return "";
    }

    public void add(QBChatMessage message) {
        chatMessages.add(message);
    }

    public void add(List<QBChatMessage> messages) {
        chatMessages.addAll(messages);
    }

    private void setAlignment(ViewHolder holder, boolean isOutgoing) {
        if (!isOutgoing) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(lp);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            holder.txtInfo.setLayoutParams(layoutParams);
            if (holder.txtMessage != null) {
                holder.contentWithBG.setBackgroundResource(R.drawable.incoming_message_bg);
                layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
                layoutParams.gravity = Gravity.RIGHT;
                holder.txtMessage.setLayoutParams(layoutParams);
            } else {
                holder.contentWithBG.setBackgroundResource(android.R.color.transparent);
            }
        } else {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.content.setLayoutParams(lp);

            layoutParams = (LinearLayout.LayoutParams) holder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            holder.txtInfo.setLayoutParams(layoutParams);

            if (holder.txtMessage != null) {
                holder.contentWithBG.setBackgroundResource(R.drawable.outgoing_message_bg);
                layoutParams = (LinearLayout.LayoutParams) holder.txtMessage.getLayoutParams();
                layoutParams.gravity = Gravity.LEFT;
                holder.txtMessage.setLayoutParams(layoutParams);
            } else {
                holder.contentWithBG.setBackgroundResource(android.R.color.transparent);
            }
        }
    }

    private ViewHolder createViewHolder(View v) {
        ViewHolder holder = new ViewHolder();
        holder.txtMessage = (TextView) v.findViewById(R.id.txtMessage);
        holder.content = (LinearLayout) v.findViewById(R.id.content);
        holder.contentWithBG = (LinearLayout) v.findViewById(R.id.contentWithBackground);
        holder.txtInfo = (TextView) v.findViewById(R.id.txtInfo);
        holder.stickerView = (ImageView) v.findViewById(R.id.sticker_image);
        return holder;
    }

    private String getTimeText(QBChatMessage message) {
        return TimeUtils.millisToLongDHMS(message.getDateSent() * 1000);
    }

    private static class ViewHolder {
        public TextView txtMessage;
        public TextView txtInfo;
        public LinearLayout content;
        public LinearLayout contentWithBG;
        public ImageView stickerView;
    }




}
