package com.artezio;

import android.app.DialogFragment;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.artezio.model.Price;
import com.artezio.tasks.AddPriceTask;
import com.artezio.util.Utils;
import org.json.JSONException;

/**
 * User: araigorodskiy
 * Date: 12.07.12
 * Time: 12:36
 */
public class AddPriceDialogFragment extends DialogFragment {

    public static AddPriceDialogFragment newInstance(String code) {
        AddPriceDialogFragment frag = new AddPriceDialogFragment();
        Bundle args = new Bundle();
        args.putString("code", code);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.addprice, container, false);
        TextView text = (TextView) inflate.findViewById(R.id.addPriceNameLabel);
        text.setText(getArguments().getString("code"));
        final EditText price = (EditText) inflate.findViewById(R.id.addPriceDialogPrice);
        Button button = (Button) inflate.findViewById(R.id.addPriceDialogAddButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // When button is clicked, call up to owning activity.
                Price p = new Price();
                try {
                    p.put(Constants.JSON.ITEM, getArguments().getString("code"));
                } catch (JSONException e) {
                    //
                }
                p.setPrice(Double.parseDouble(price.getText().toString()));
                Location l = Utils.getLocation(getActivity());
                p.setLatitude(l.getLatitude());
                p.setLongitude(l.getLongitude());
                new AddPriceTask(getActivity()).execute(p);
                dismiss();
            }
        });
        Button buttonCancel = (Button) inflate.findViewById(R.id.addPriceDialogCancelButton);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dismiss();
            }
        });
        return inflate;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
/*@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setPositiveButton(R.string.addPriceDialogAddButton,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        }
                )
                .setNegativeButton(R.string.addPriceDialogCancelButton,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        }
                )
                .create();
    }*/

}
