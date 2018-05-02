package com.example.vloboda.dynamicentityeditor;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bl.FirmEditablePresenter;
import com.example.bl.ui.DynamicView;
import com.example.bl.ui.FirmEditableView;
import com.example.model.FirmDto;

public class FirmEditableViewImpl extends DialogFragment implements FirmEditableView {

    private FirmDto dto;
    private ViewGroup mainView;
    private FirmEditablePresenter presenter;
    private View vUndoButton;
    private LinearLayout vContent;
    private MainActivity mainActivity;

    public static FirmEditableViewImpl createView(final FirmDto dto, final MainActivity mainActivity) {
        final FirmEditableViewImpl result = new FirmEditableViewImpl();
        result.dto = dto;
        result.mainActivity = mainActivity;

        return result;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        presenter = new FirmEditablePresenter(
                mainActivity.getDynamicAttributesFactory(),
                mainActivity.getFieldsConfigurationService(),
                mainActivity.getEditorTemplateManager(),
                mainActivity.getLocationManager(),
                mainActivity.getRepository(),
                mainActivity.getToaster());

        this.mainView = (ViewGroup) inflater.inflate(R.layout.firm_editable_view, container, false);

        this.mainView.findViewById(R.id.fev_back_button)
                .setOnClickListener(v -> presenter.onBackButtonClick());

        this.mainView.findViewById(R.id.fev_ok_button)
                .setOnClickListener(v -> presenter.onOkClick());

        this.mainView.findViewById(R.id.fev_delete_button)
                .setOnClickListener(v -> presenter.onDeleteButtonClick());

        vContent = this.mainView.findViewById(R.id.fev_content);
        vUndoButton = this.mainView.findViewById(R.id.fev_undo_button);
        vUndoButton.setOnClickListener(v -> presenter.onUndoChangesClick());

        this.presenter.init(this.dto, this);

        return this.mainView;
    }

    @Override
    public void setTitle(String title) {
        ((TextView) this.mainView.findViewById(R.id.fev_title)).setText(title);
    }

    @Override
    public void showUndoButton(boolean show) {
        vUndoButton.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void closeView() {
        dismiss();

        refreshList();
    }

    @Override
    public void onChangeView(DynamicView view) {

    }

    @Override
    public Object inflateLayout(int viewId) {
        return inflateLayout(viewId, false);
    }

    @Override
    public Object inflateLayout(int viewId, boolean attachToRoot) {
        return inflateLayout(viewId, attachToRoot);
    }

    @Override
    public void addLayoutView(DynamicView view) {
        vContent.addView((View) view.getView());
    }

    @Override
    public void showCloseAlertDialog() {
        DynamicAlertDialogImpl dialog = new DynamicAlertDialogImpl(getContext());
        dialog.showDialog(R.string.unsaved_changes_alert,
                R.string.continue_editing, () -> dialog.dismiss(),
                R.string.remove_unsaved_changes, () ->  { presenter.closeView(); dialog.dismiss(); });
    }

    @Override
    public void showUndoAlertDialog() {
        DynamicAlertDialogImpl dialog = new DynamicAlertDialogImpl(getContext());
        dialog.showDialog(R.string.revert_changes_alert,
                R.string.cancel, () -> dialog.dismiss(),
                R.string.remove_unsaved_changes, () ->  { presenter.undoChanges(); dialog.dismiss(); });
    }

    @Override
    public void refreshList() {
        mainActivity.refreshList();
    }
}
