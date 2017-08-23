package test.oubowu.com.tablayoutscrollexample;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;

/**
 * 展示网络图片的fragment
 */
public class HomeSimpleFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;
    private GridLayoutManager mManager;
    private View mView;
    /**
     * 储存图片位置的list
     */
    private ArrayList<String> mProductInfos;

    public HomeSimpleFragment() {
    }

    /**
     * 订阅一个返回顶部的事件
     *
     * @param element
     */
    @Subscriber(tag = "scrollToTop")
    private void scrollTotop(EventBusElement element) {
        mRecyclerView.smoothScrollToPosition(0);
        element.setAppBarOffset(-MainActivity.mTabLayoutHeight);
        EventBus.getDefault().post(element, "scroollTabLayout");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 缓存view的状态
        if (null == mView) {
            mView = initView(inflater, container);
            initData();
        } else {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (null != parent) {
                parent.removeView(mView);
            }
        }
        return mView;
    }

    private View initView(LayoutInflater inflater, ViewGroup container) {
        mView = inflater.inflate(R.layout.fragment_home_simple, container, false);

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_view);

        return mView;
    }


    private void initData() {

        mProductInfos = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            mProductInfos.add("http://img3.imgtn.bdimg.com/it/u=3409482129,3964399276&fm=21&gp=0.jpg");
        }

        // 网格布局，每行两列
        mManager = new GridLayoutManager(getActivity(), 2);
        mManager.setSmoothScrollbarEnabled(true);
        // 设置自定义的SpanSizeLookup，位置为0的地方占两列，其它占一列
        mManager.setSpanSizeLookup(new HeaderSpanSizeLookup(mManager));
        mRecyclerView.setLayoutManager(mManager);

        mAdapter = new RecyclerAdapter();
        mRecyclerView.setAdapter(mAdapter);

        final EventBusElement element = new EventBusElement();

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                    element.setAppBarOffset(dy);
                    EventBus.getDefault().post(element, "scroollTabLayout");
            }
        });
    }

    class HeaderSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

        private final GridLayoutManager layoutManager;

        public HeaderSpanSizeLookup(GridLayoutManager layoutManager) {
            this.layoutManager = layoutManager;
        }

        @Override
        public int getSpanSize(int position) {
            return position == 0 ? layoutManager.getSpanCount() : 1;
        }

    }

    private class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {


        class RecyclerViewHolder extends RecyclerView.ViewHolder {

            // 加载网络图片
            ImageView mProductImage;

            // 跟tablayout同高的view，为了帧布局那里的tablayout不覆盖掉图片的内容
            View mView;

            public RecyclerViewHolder(View itemView) {
                super(itemView);
                mProductImage = (ImageView) itemView.findViewById(R.id.siv_home_product);
                mView = (View) itemView.findViewById(R.id.view_blank_header);
            }
        }

        /**
         * 图片布局对应的type
         */
        private final int VIEW_TYPE_REPLY = 1;
        /**
         * tablayout同高的空布局的type
         */
        private final int VIEW_TYPE_BLANK = 2;

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerViewHolder viewHolder;
            switch (viewType) {
                case VIEW_TYPE_BLANK:
                    viewHolder = new RecyclerViewHolder
                            (LayoutInflater.from(getActivity()).inflate(R.layout.blank_header, parent, false));
                    return viewHolder;
                case VIEW_TYPE_REPLY:
                    viewHolder = new RecyclerViewHolder
                            (LayoutInflater.from(getActivity()).inflate(R.layout.recycleview_item, parent, false));
                    return viewHolder;
                default:
                    return null;
            }
        }

        /**
         * 图片所在的布局，用于设置图片宽高
         */
        private RelativeLayout.LayoutParams mProductLp;

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
            switch (getItemViewType(position)) {
                case VIEW_TYPE_BLANK:
                    ViewGroup.LayoutParams lp = mView.getLayoutParams();
                    // 这里是设置此view高度为tablayout的高度，由此tablayout就不会覆盖底下的图片
                    lp.height = MainActivity.mTabLayoutHeight;
                    holder.mView.setLayoutParams(lp);
                    break;
                case VIEW_TYPE_REPLY:
                    if (mProductLp == null) {
                        mProductLp = (RelativeLayout.LayoutParams) holder.mProductImage.getLayoutParams();
                        DisplayMetrics metrics = new DisplayMetrics();
                        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                        // 图片宽高设为屏幕的一半长度
                        mProductLp.width = mProductLp.height = metrics.widthPixels / 2;
                    }

                    holder.mProductImage.setLayoutParams(mProductLp);

                    // 使用glide加载网络图片，mProductInfos.get(position - 1)是因为0位置是头布局
                    Glide.with(HomeSimpleFragment.this)
                            .load(Uri.parse(mProductInfos.get(position - 1)))
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .placeholder(R.mipmap.ic_launcher)
                            .into(holder.mProductImage);

                    break;

            }

        }


        @Override
        public int getItemViewType(int position) {
            // 根据位置设置view的type
            if (position == 0) {
                position = VIEW_TYPE_BLANK;
            } else {
                position = VIEW_TYPE_REPLY;
            }
            return position;
        }

        @Override
        public int getItemCount() {
            // 因为加了一个头布局，所以size加1
            return mProductInfos.size() + 1;
        }

    }

}
