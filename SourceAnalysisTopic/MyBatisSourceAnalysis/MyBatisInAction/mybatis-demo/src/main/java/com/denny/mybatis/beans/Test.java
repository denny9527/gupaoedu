package com.denny.mybatis.beans;

public class Test {
    private Integer id;

    private Integer nums;

    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNums() {
        return nums;
    }

    public void setNums(Integer nums) {
        this.nums = nums;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Test{");
        sb.append("id=").append(id);
        sb.append(", nums=").append(nums);
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }
}