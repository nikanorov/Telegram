package org.telegram.ui.Cells;


import android.content.Context;
import android.graphics.Canvas;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;

public class ProfileMsgAsCell extends FrameLayout {

    private BackupImageView imageView;
    private TextView nameTextView;
    private TextView subNameTextView;

    private CheckBox2 checkBox;
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private TLRPC.User user;
    private TLRPC.Chat chat;

    private final Theme.ResourcesProvider resourcesProvider;


    public ProfileMsgAsCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        setWillNotDraw(false);

        LinearLayout rootLayout = new LinearLayout(getContext());
        rootLayout.setOrientation(LinearLayout.HORIZONTAL);

        FrameLayout imageLayout = new FrameLayout(getContext());

        imageView = new BackupImageView(context);
        imageView.setRoundRadius(AndroidUtilities.dp(28));
        imageLayout.addView(imageView, LayoutHelper.createFrame(48, 48, Gravity.TOP | Gravity.LEFT, 16, 7, 7, 7));

        checkBox = new CheckBox2(context, 21, resourcesProvider);
        checkBox.setColor(Theme.key_dialogRoundCheckBox, Theme.key_dialogBackground, Theme.key_dialogRoundCheckBoxCheck);
        checkBox.setDrawUnchecked(false);
        checkBox.setDrawBackgroundAsArc(4);
        checkBox.setProgressDelegate(progress -> {
            float scale = 1.0f - (1.0f - 0.857f) * checkBox.getProgress();
            imageView.setScaleX(scale);
            imageView.setScaleY(scale);
            invalidate();
        });

        LinearLayout bothLayout = new LinearLayout(getContext());
        bothLayout.setOrientation(LinearLayout.HORIZONTAL);
        bothLayout.addView(imageLayout);

        LinearLayout textLayout = new LinearLayout(getContext());
        textLayout.setOrientation(LinearLayout.VERTICAL);

        nameTextView = new TextView(context);
        nameTextView.setTextColor(getThemedColor( Theme.key_chats_name));
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        nameTextView.setPaintFlags(TextPaint.ANTI_ALIAS_FLAG);
        nameTextView.setMaxLines(1);
        nameTextView.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        nameTextView.setLines(1);
        nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        textLayout.addView(nameTextView,LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

        subNameTextView = new TextView(context);
        subNameTextView.setTextColor(getThemedColor( Theme.key_windowBackgroundWhiteGrayText3));
        subNameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        subNameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
        subNameTextView.setPaintFlags(TextPaint.ANTI_ALIAS_FLAG);
        subNameTextView.setMaxLines(1);
        subNameTextView.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        subNameTextView.setLines(1);
        subNameTextView.setEllipsize(TextUtils.TruncateAt.END);
        textLayout.addView(subNameTextView,LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));


        bothLayout.addView(textLayout,LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL | Gravity.LEFT, 7, 7, 16, 7));

        addView(bothLayout, LayoutHelper.createFrame(LayoutHelper.WRAP_CONTENT, LayoutHelper.WRAP_CONTENT));

    }



    public TLRPC.User getUser() {
        return user;
    }

    public TLRPC.Chat getChat() {
        return chat;
    }

    public void setData(TLObject object, TLRPC.EncryptedChat ec, CharSequence n, CharSequence s, boolean needCount, boolean saved) {
        chat = null;
        user = null;
        if (object instanceof TLRPC.User) {
            user = (TLRPC.User) object;
            chat = null;
        } else if (object instanceof TLRPC.Chat) {
            chat = (TLRPC.Chat) object;
            user = null;
        }
        if (user!=null) {
            avatarDrawable.setInfo(user);

            if (n != null) {
                nameTextView.setText(n);
            } else if (user != null) {
                nameTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
            } else {
                nameTextView.setText("");
            }
            imageView.setForUserOrChat(user, avatarDrawable);
        } else {
            if (n != null) {
                nameTextView.setText(n);
            } else if (chat != null) {
                nameTextView.setText(chat.title);
            } else {
                nameTextView.setText("");
            }
            avatarDrawable.setInfo(chat);
            imageView.setForUserOrChat(chat, avatarDrawable);
        }

        if (s != null) {
            subNameTextView.setText(s);
        }
    }

    public void setChecked(boolean checked, boolean animated) {
        checkBox.setChecked(checked, animated);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int cx = imageView.getLeft() + imageView.getMeasuredWidth() / 2;
        int cy = imageView.getTop() + imageView.getMeasuredHeight() / 2;
        Theme.checkboxSquare_checkPaint.setColor(getThemedColor(Theme.key_dialogRoundCheckBox));
        Theme.checkboxSquare_checkPaint.setAlpha((int) (checkBox.getProgress() * 255));
        canvas.drawCircle(cx, cy, AndroidUtilities.dp(24), Theme.checkboxSquare_checkPaint);
    }

    private int getThemedColor(String key) {
        Integer color = resourcesProvider != null ? resourcesProvider.getColor(key) : null;
        return color != null ? color : Theme.getColor(key);
    }
}
