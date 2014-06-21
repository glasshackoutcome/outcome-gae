package com.appspot.ghackoutcome.dao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rac on 21.06.14.
 */
public class Participant implements Serializable{
    private String firstName;
    private String lastName;
    private String photoURI;
    private String programName;
    private String valuedOutcome;
    private String methodOne;
    private String methodTwo;
    private String methodThree;
    private String emergDial;
    private String emergName;
    private String emergInfo;
    private boolean methodOneCompleted;
    private boolean methodTwoCompleted;
    private boolean methodThreeCompleted;

    public String getFirstName() {
        return firstName;
    }

    public Participant setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public Participant setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getPhotoURI() {
        return photoURI;
    }

    public Participant setPhotoURI(String photoURI) {
        this.photoURI = photoURI;
        return this;
    }

    public String getProgramName() {
        return programName;
    }

    public Participant setProgramName(String programName) {
        this.programName = programName;
        return this;
    }

    public String getValuedOutcome() {
        return valuedOutcome;
    }

    public Participant setValuedOutcome(String valuedOutcome) {
        this.valuedOutcome = valuedOutcome;
        return this;
    }

    public String getMethodOne() {
        return methodOne;
    }

    public Participant setMethodOne(String methodOne) {
        this.methodOne = methodOne;
        return this;
    }

    public String getMethodTwo() {
        return methodTwo;
    }

    public Participant setMethodTwo(String methodTwo) {
        this.methodTwo = methodTwo;
        return this;
    }

    public String getMethodThree() {
        return methodThree;
    }

    public Participant setMethodThree(String methodThree) {
        this.methodThree = methodThree;
        return this;
    }

    public String getEmergDial() {
        return emergDial;
    }

    public Participant setEmergDial(String emergDial) {
        this.emergDial = emergDial;
        return this;
    }

    public String getEmergName() {
        return emergName;
    }

    public Participant setEmergName(String emergName) {
        this.emergName = emergName;
        return this;
    }

    public String getEmergInfo() {
        return emergInfo;
    }

    public Participant setEmergInfo(String emergInfo) {
        this.emergInfo = emergInfo;
        return this;
    }

    public boolean isMethodOneCompleted() {
        return methodOneCompleted;
    }

    public Participant setMethodOneCompleted(boolean methodOneCompleted) {
        this.methodOneCompleted = methodOneCompleted;
        return this;
    }

    public boolean isMethodTwoCompleted() {
        return methodTwoCompleted;
    }

    public Participant setMethodTwoCompleted(boolean methodTwoCompleted) {
        this.methodTwoCompleted = methodTwoCompleted;
        return this;
    }

    public boolean isMethodThreeCompleted() {
        return methodThreeCompleted;
    }

    public Participant setMethodThreeCompleted(boolean methodThreeCompleted) {
        this.methodThreeCompleted = methodThreeCompleted;
        return this;
    }

    /**
     * Creates a hashmap to be used for substitution.
     * @return hashmap of variables to be replaced in template
     */
    public Map<String, String> getMap() {
        Map<String, String> ret = new HashMap<String, String>();
        ret.put("lastName", getLastName());
        ret.put("firstName", getFirstName());
        ret.put("photoURI", getPhotoURI());
        ret.put("programName", getProgramName());
        ret.put("valuedOutcome", getValuedOutcome());
        ret.put("methodOne", getMethodOne());
        ret.put("methodTwo", getMethodTwo());
        ret.put("methodThree", getMethodThree());
        ret.put("emergDial", getEmergDial());
        ret.put("emergName", getEmergName());
        ret.put("emergInfo", getEmergInfo());
        return ret;
    }
}
