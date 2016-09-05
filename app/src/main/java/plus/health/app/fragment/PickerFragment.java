package plus.health.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import plus.health.app.interfaces.PickerDialogListener;
import plus.health.app.R;

/**
 * Created by Saquib on 17-Jul-16.
 */

public class PickerFragment extends BottomSheetDialogFragment {
    private String type;

    public static PickerFragment newInstance() {
        PickerFragment frag = new PickerFragment();
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_picker_fragment, container);
        View camera =  view.findViewById(R.id.camera_menu);
        View folder =  view.findViewById(R.id.folder_menu);
        type = getArguments().getString("TYPE");
        final PickerDialogListener listener = (PickerDialogListener) getTargetFragment();
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCameraSelected(type);
                PickerFragment.this.dismiss();
            }
        });
        folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFolderSelected(type);
                PickerFragment.this.dismiss();
            }
        });
        return view;
    }


}
