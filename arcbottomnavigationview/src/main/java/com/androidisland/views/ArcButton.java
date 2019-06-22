package com.androidisland.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.ContextThemeWrapper;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.TextViewCompat;
import com.google.android.material.button.MaterialButton;

/**
 * Created by Farshad Tahmasbi on June 22,2019.
 * Copyright(c) 2019, All rights reserved.
 * https://github.com/FarshadTahmasbi/ArcBottomNavigationView
 * Email: farshad.tmb@gmail.com
 */
public class ArcButton extends MaterialButton {

    private int iconSize = 0;
    private Drawable icon = null;
    private PorterDuff.Mode iconTintMode = PorterDuff.Mode.SRC_IN;
    private ColorStateList iconTint = null;
    private int iconLeft = 0;

    public ArcButton(Context context) {
        super(new ContextThemeWrapper(context, R.style.ArcTheme), null, R.attr.materialButtonStyle);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (iconSize == 0) {
            setIconSize(w / 2);
        }
    }

    @Override
    public void setIconSize(int iconSize) {
        if (iconSize < 0) {
            throw new IllegalArgumentException("iconSize cannot be less than 0");
        } else {
            if (this.iconSize != iconSize) {
                this.iconSize = iconSize;
                updateIcon();
            }
        }
    }

    @Override
    public int getIconSize() {
        return iconSize;
    }

    @Override
    public void setIcon(Drawable icon) {
        if (this.icon != icon) {
            this.icon = icon;
            updateIcon();
        }
    }

    @Override
    public Drawable getIcon() {
        return icon;
    }

    @Override
    public void setIconTint(@Nullable ColorStateList iconTint) {
        if (this.iconTint != iconTint) {
            this.iconTint = iconTint;
            updateIcon();
        }
    }

    @Override
    public ColorStateList getIconTint() {
        return iconTint;
    }

    @Override
    public void setIconTintMode(PorterDuff.Mode iconTintMode) {
        if (this.iconTintMode != iconTintMode) {
            this.iconTintMode = iconTintMode;
            updateIcon();
        }
    }

    @Override
    public PorterDuff.Mode getIconTintMode() {
        return iconTintMode;
    }

    private void updateIcon() {
        if (this.icon != null) {
            this.icon = this.icon.mutate();
            DrawableCompat.setTintList(this.icon, this.iconTint);
            if (this.iconTintMode != null) {
                DrawableCompat.setTintMode(this.icon, this.iconTintMode);
            }

            int width = this.iconSize != 0 ? this.iconSize : this.icon.getIntrinsicWidth();
            int height = this.iconSize != 0 ? this.iconSize : this.icon.getIntrinsicHeight();
            iconLeft = (getMeasuredWidth() - width) / 2;
            if (iconLeft < 0) iconLeft = 0;
            this.icon.setBounds(iconLeft, 0, width + iconLeft, height);
        }
        TextViewCompat.setCompoundDrawablesRelative(this, this.icon, (Drawable) null, (Drawable) null, (Drawable) null);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
        iconLeft = (getMeasuredWidth() - icon.getIntrinsicWidth()) / 2;
        updateIcon();
    }
}
