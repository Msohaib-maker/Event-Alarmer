package com.sohaib.eventcheck.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sohaib.eventcheck.Interface.BottomSheetListener;
import com.sohaib.eventcheck.Model.ListModel;
import com.sohaib.eventcheck.R;
import com.sohaib.eventcheck.Utility.Padding;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.OnColorSelectedListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetFrag extends BottomSheetDialogFragment {

    private BottomSheetListener mListener;
    private ColorPickerView colorPicker;
    private int iconColor;

    public BottomSheetFrag() {
        // Required empty public constructor
    }


    public  static BottomSheetFrag newInstance(ListModel listModel)
    {
        BottomSheetFrag bottomSheetFrag = new BottomSheetFrag();

        if(listModel != null)
        {
            Bundle args = new Bundle();
            args.putSerializable("key_list_model",listModel);
            bottomSheetFrag.setArguments(args);
        }

        return bottomSheetFrag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.bottomsheet, container, false);

        EditText ListName = view.findViewById(R.id.InputListNameId);
        TextView DoneBtn = view.findViewById(R.id.DoneId);
        TextView CancelBtn = view.findViewById(R.id.cancelId);
        ImageView Icon = view.findViewById(R.id.listIconId);
        colorPicker = view.findViewById(R.id.colorPicker);

        iconColor = 0xFF1976D2;

        Bundle args1 = getArguments();

        if(args1 != null)
        {
            ListModel listModel = (ListModel) getArguments().getSerializable("key_list_model");

            ListName.setText(listModel.getName());
            colorPicker.setColor(listModel.getIconColor(),true);
            DoneBtn.setTextColor(Color.rgb(25,118,210));
            DoneBtn.setEnabled(true);

            DoneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listModel.setIconColor(iconColor);
                    mListener.OnUpdate(listModel,Icon);
                }
            });

        }


        ListName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean isInputEmpty = TextUtils.isEmpty(s);
                DoneBtn.setEnabled(!isInputEmpty);




                if(!isInputEmpty)
                {
                    DoneBtn.setTextColor(Color.rgb(25,118,210));
                    DoneBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Padding padding = new Padding();
                            padding.paddingLeft = Icon.getPaddingLeft();
                            padding.paddingRight = Icon.getPaddingRight();
                            padding.paddingBottom = Icon.getPaddingBottom();
                            padding.paddingTop = Icon.getPaddingTop();
                            if(args1 != null)
                            {
                                assert getArguments() != null;
                                ListModel listModel = (ListModel) getArguments().getSerializable("key_list_model");
                                listModel.setName(ListName.getText().toString());
                                listModel.setIconColor(iconColor);
                                mListener.OnUpdate(listModel,Icon);
                            }
                            else
                            {
                                mListener.onDoneButtonClicked(ListName.getText().toString(),Icon,iconColor);
                            }

                        }
                    });
                }
                else
                {
                    DoneBtn.setTextColor(Color.rgb(128,128,128));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onCancelButtonClicked();
            }
        });

        colorPicker.addOnColorSelectedListener(new OnColorSelectedListener() {
            @Override
            public void onColorSelected(int selectedColor) {
                //Toast.makeText(getContext(), "selected color : " + String.valueOf(selectedColor), Toast.LENGTH_SHORT).show();
                iconColor = selectedColor;
                Icon.setBackgroundColor(selectedColor);
            }
        });





        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement BottomSheetListener");
        }
    }
}
