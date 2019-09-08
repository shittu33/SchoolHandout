package com.example.amazing_picker.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.amazing_picker.R;
import com.example.amazing_picker.Veiws.SelectableViewHolder;
import com.example.amazing_picker.models.Selectable_image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.amazing_picker.utilities.ImageCursorLoaderUtils.PIC_TAG;


/**
 * Created by Abu Muhsin on 17/12/2018.
 */

public class Images_recyler_adapter extends RecyclerView.Adapter<SelectableViewHolder>
        implements SelectableViewHolder.OnRecylerItemSelected {

    private static final String recyclerTAG = "recycler";
    private Context context;
    private ArrayList<Selectable_image> selectable_images;
    private ArrayList<Selectable_image> indexed_selectable_images;
    private boolean isMultiSelectionEnabled;
    private SelectableViewHolder.OnRecylerItemSelected selectionListener;

    public Images_recyler_adapter(Context context, ArrayList<Selectable_image> images,
                                  SelectableViewHolder.OnRecylerItemSelected onRecylerItemSelected,
                                  boolean is_multiSelectionAllowed) {
        Log.i(recyclerTAG, "Recycler Adapter constructor: called ");
        this.context = context;
        this.selectionListener = onRecylerItemSelected;
        this.isMultiSelectionEnabled = is_multiSelectionAllowed;
        selectable_images = images;
        indexed_selectable_images = new ArrayList<>();


    }

    @Override
    public SelectableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Log.i(recyclerTAG, " onCreateViewHolder: called ");
        View v = LayoutInflater.from(context).inflate(R.layout.picker_recycler_item, parent, false);
        return new SelectableViewHolder(v, this);
    }

    @Override
    public void onBindViewHolder(final SelectableViewHolder holder, final int position) {
        Selectable_image selectable_image = selectable_images.get(position);
        String selectable_path = selectable_image.getSelectable_path();
        holder.img.setImageResource(R.drawable.ic_radio_button_unchecked_black_24dp);

        Glide.with(context)
                .load(Uri.fromFile(new File(selectable_path)))
                .thumbnail(0.3f)
                .into(holder.img);
        holder.mImage = selectable_image;
        holder.setChecked(holder.mImage.isSelected());
        Log.i(recyclerTAG, " Picker_images_recycler_adapter ->  onBindViewHolder: finished ");
    }

    @Override
    public void onImageSelected(Selectable_image selected_image) {
        if (!isMultiSelectionEnabled) {

            for (Selectable_image selectable_image : selectable_images) {
                if (!selectable_image.equals(selected_image) && selectable_image.isSelected()) {
                    selectable_image.set_selected(false);
                } else if (selectable_image.equals(selected_image) && selected_image.isSelected()) {
                    selectable_image.set_selected(true);
                }
            }
            notifyDataSetChanged();
        }
        if (selected_image.isSelected()) {
            indexed_selectable_images.add(selected_image);
        } else {
            indexed_selectable_images.remove(selected_image);
        }
        selectionListener.onImageSelected(selected_image);
    }

    public ArrayList<Selectable_image> getIndexed_selectable_images() {
        if (indexed_selectable_images.isEmpty()) {
            Log.i(PIC_TAG, "indexed_selectable_images is empty ");
        }
        return indexed_selectable_images;
    }

    public List<Selectable_image> getSelectedItems() {

        List<Selectable_image> selectedItems = new ArrayList<>();
        for (Selectable_image item : selectable_images) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }

    @Override
    public int getItemViewType(int position) {
        if (isMultiSelectionEnabled) {
            return SelectableViewHolder.MULTI_SELECTION;
        } else {
            return SelectableViewHolder.SINGLE_SELECTION;
        }
    }

    @Override
    public int getItemCount() {
        return selectable_images.size();
    }

}
