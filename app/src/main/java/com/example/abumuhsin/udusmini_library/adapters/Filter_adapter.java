package com.example.abumuhsin.udusmini_library.adapters;

//import com.example.abumuhsin.udusmini_library.utils.GlideApp;

//import jp.co.cyberagent.android.gpuimage.GPUImage;
//import jp.co.cyberagent.android.gpuimage.GPUImageView;
//import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;
//import jp.wasabeef.glide.transformations.gpu.GPUFilterTransformation;

/**
 * Created by Abu Muhsin on 17/12/2018.
 */
public class Filter_adapter /*extends RecyclerView.Adapter<Filter_adapter.ViewHolder>*/ {
//    private static final String recyclerTAG = "recycler";
//    Context context;
//    Page_EditingActivity page_editingActivity;
////    ArrayList<GPUImageFilt er> filters;
//    Bitmap image_bitmap;
//    public ViewHolder viewHolder;
//
//    public Filter_adapter(Context context, Bitmap image, ArrayList<GPUImageFilter> filters) {
//        Log.i(recyclerTAG, "Recycler Adapter constructor: called ");
//        this.context = context;
//        this.filters = filters;
//        image_bitmap = image;
//        page_editingActivity = (Page_EditingActivity) context;
//
//    }
//    GPUImage gpuImage;
//    @Override
//    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        Log.i(recyclerTAG, " onCreateViewHolder: called ");
//        View v = LayoutInflater.from(context).inflate(R.layout.filter_recycler_item, parent, false);
//        viewHolder = new ViewHolder(v);
//        gpuImage = new GPUImage(context);
//        gpuImage.setGLSurfaceView(viewHolder.img);
//        gpuImage.setCover(image_bitmap);
//        return new ViewHolder(v);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
//        Log.i(recyclerTAG, " onBindViewHolder: called ");
//        viewHolder = holder;
//        gpuImage.setFilter(filters.get(position));
////        holder.img.setCover(image_bitmap);
////        holder.img.setFilter(filters.get(position));
//
////            GlideApp.with(context)
////                    .load(image_bitmap)
////                    .placeholder(R.drawable.trimed_logo)
//////                    .transform(filters.get(position))
////                    .into(holder.img);
//
//
//        holder.img.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onFilerSelectedListetener.onFilerSelected(position, filters.get(position));
//            }
//        });
//        final String msg = "dragDebug";
//    }
//
//    @Override
//    public int getItemCount() {
//        return filters.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        private GLSurfaceView img;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            img = itemView.findViewById(R.id.img);
//        }
//    }
//
//    private OnFilerSelectedListener onFilerSelectedListetener;
//
//    public void setOnFilerSelected(OnFilerSelectedListener onFilerSelected) {
//        this.onFilerSelectedListetener = onFilerSelected;
//    }
//
//    public interface OnFilerSelectedListener {
//        public void onFilerSelected(int position, GPUImageFilter selected_filter);
//    }
}
