package com.gupao.edu.vip.course.chaper2;

public class StackAllocation {

    public StackAllocation obj;

    /**
     * 逃逸
     * @return
     */
    public StackAllocation getInstance(){
        return  obj == null?new StackAllocation():obj;
    }

    /**
     * 逃逸
     * @return
     */
    public void setObj() {
        this.obj = new StackAllocation();
    }

    /**
     * 没有逃逸
     * @return
     */
    public void useStackAllocation(){
        StackAllocation stackAllocation = new StackAllocation();
    }
}
