package com.example.vloboda.dynamicentityeditor.dynamic;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.model.EditObjectTemplateDto;
import com.example.vloboda.dynamicentityeditor.AndroidHelpers;
import com.example.vloboda.dynamicentityeditor.R;

import net.cachapa.expandablelayout.ExpandableLayout;

public class DynamicViewGroupControl extends RelativeLayout {
    private final LayoutInflater inflater;
    private TextView caption;
    private ExpandableLayout expandLayout;
    private View expandHeader;
    private View expandArrow;
    private View dashLine;
    private View vSpace;
    private View vDashSpace;

    public DynamicViewGroupControl(Context context) {
        super(context);
        inflater = LayoutInflater.from(context);
        init();
    }

    public DynamicViewGroupControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflater = LayoutInflater.from(context);
        init();
    }

    private void init() {
        View vMainView = inflater.inflate(R.layout.dynamic_view_group_control, this, true);
        caption = vMainView.findViewById(R.id.dvc_caption);
        expandHeader = vMainView.findViewById(R.id.expand_header);
        expandArrow = vMainView.findViewById(R.id.arrow);
        dashLine = vMainView.findViewById(R.id.dvc_dl);
        expandLayout = vMainView.findViewById(R.id.dvc_main_content);
        vSpace = vMainView.findViewById(R.id.dvgc_space);
        vDashSpace = vMainView.findViewById(R.id.dvgc_dash_space);

        expandLayout.setOnExpansionUpdateListener((expansionFraction, state) -> {
            if(state == ExpandableLayout.State.EXPANDING) {
                createRotateAnimator(expandArrow, 0f, 180f).start();
            }
            if(state == ExpandableLayout.State.COLLAPSING) {
                createRotateAnimator(expandArrow, 180f, 0f).start();
            }
        });

        this.expandHeader.setOnClickListener(v -> {
            if(expandLayout.isExpanded()) {
                expandLayout.collapse();
                vSpace.setVisibility(VISIBLE);
                vDashSpace.setVisibility(GONE);
            } else {
                expandLayout.expand();
                vSpace.setVisibility(GONE);

                if (dashLine.getVisibility() == VISIBLE) {
                    vDashSpace.setVisibility(VISIBLE);
                }
            }
        });
    }

    private ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
        animator.setDuration(100);
        animator.setInterpolator(new LinearInterpolator());
        return animator;
    }

    public void setCaption(String caption) {
        this.caption.setText(caption);
    }

    public void addDynamicView(View view) {
        if(view.getLayoutParams() == null) {
            FrameLayout.LayoutParams lp =
                    new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(AndroidHelpers.getPixelsFromDp(getContext(), 20), 0, 0, 0);
            view.setLayoutParams(lp);
        }

        view.setTag(R.id.dynamic_view_group, this);

        expandLayout.addView(view);
    }

    public void setTemplate(EditObjectTemplateDto.ItemDto template) {
        if(!template.getExpandable()) {
            this.expandArrow.setVisibility(GONE);
            this.expandHeader.setOnClickListener(v -> {
            });
        }

        if(template.getExpanded()) {
            this.expandLayout.setExpanded(true, true);
            this.vSpace.setVisibility(GONE);
            this.vDashSpace.setVisibility(template.getShowDashLine() ? VISIBLE : GONE);
        }

        this.expandHeader.setVisibility(template.getShowHeader() ? VISIBLE : GONE);

        this.dashLine.setVisibility(template.getShowDashLine() ? VISIBLE : GONE);

        if (!template.getBoldCaption()) {
            caption.setTypeface(Typeface.DEFAULT);
        }

        if (template.getCaption() != null) {
            setCaption(template.getCaption());
        }
    }

    public void expand() {
        this.expandLayout.setExpanded(true, false);
        this.vSpace.setVisibility(GONE);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.WRAP_CONTENT;

        setLayoutParams(params);
    }
}

