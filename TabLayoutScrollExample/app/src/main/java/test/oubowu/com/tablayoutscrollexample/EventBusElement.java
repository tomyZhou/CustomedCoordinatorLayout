package test.oubowu.com.tablayoutscrollexample;

/**
 * EvenBus传递变量的媒介
 */
public class EventBusElement {

    public EventBusElement() {
    }


    private int mAppBarOffset;

    /**
     * 获取Layout上移的偏移量
     */
    public int getAppBarOffset() {
        return mAppBarOffset;
    }

    /**
     * 设置Layout上移的偏移量
     */
    public void setAppBarOffset(int appBarOffset) {
        this.mAppBarOffset = appBarOffset;
    }

}
