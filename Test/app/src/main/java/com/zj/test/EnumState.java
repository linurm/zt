package com.zj.test;

public enum EnumState {
	     
	    IDLE("idle"),
	    BUY("buy"),
	    SELL("selll");
	     
	    private String value;
	     
	    private EnumState(String value){
	        this.value = value;
	    }
	     
	    public String getValue(){
	        return value;
	    }
	    @Override
	    public String toString() {
	            return value;              
	    }
//	     
//	    public static void main(String[] args){
//	        System.out.println(EnumState.valueOf("A"));
//	        System.out.println(EnumState.A);
//	    }
	 
	}
