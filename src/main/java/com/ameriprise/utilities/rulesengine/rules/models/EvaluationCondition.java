/**
 * Copyright 2021 Ameriprise Financial, Inc. All rights reserved. Proprietary and Confidential. Use
 * is subject to license terms.
 */
package com.ameriprise.utilities.rulesengine.rules.models;

import java.util.List;

public class EvaluationCondition {
  String key;
  String type;
  String equals;
  String notEquals;
  List<String> oneOf;
  List<String> notOneOf;
  long withinDays;
  long beyondDays;
  String contains;
  String notContains;
  String after;
  String before;

  public EvaluationCondition(String key, String equals) {
    this.key = key;
    this.equals = equals;
  }

  public EvaluationCondition(String key, List<String> oneOf) {
    this.key = key;
    this.oneOf = oneOf;
  }

  public EvaluationCondition(String key, long withinDays) {
    this.key = key;
    this.withinDays = withinDays;
  }

  public EvaluationCondition(String key) {
    this.key = key;
  }

  public EvaluationCondition() {}

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getEquals() {
    return equals;
  }

  public void setEquals(String equals) {
    this.equals = equals;
  }

  public List<String> getOneOf() {
    return oneOf;
  }

  public void setOneOf(List<String> oneOf) {
    this.oneOf = oneOf;
  }

  public long getWithinDays() {
    return withinDays;
  }

  public void setWithinDays(long withinDays) {
    this.withinDays = withinDays;
  }

  public String getNotEquals() {
    return notEquals;
  }

  public void setNotEquals(String notEquals) {
    this.notEquals = notEquals;
  }

  public List<String> getNotOneOf() {
    return notOneOf;
  }

  public void setNotOneOf(List<String> notOneOf) {
    this.notOneOf = notOneOf;
  }

  public long getBeyondDays() {
    return beyondDays;
  }

  public void setBeyondDays(long beyondDays) {
    this.beyondDays = beyondDays;
  }

  public String getContains() {
    return contains;
  }

  public void setContains(String contains) {
    this.contains = contains;
  }

  public String getNotContains() {
    return notContains;
  }

  public void setNotContains(String notContains) {
    this.notContains = notContains;
  }

  public String getAfter() {
    return after;
  }

  public void setAfter(String after) {
    this.after = after;
  }

  public String getBefore() {
    return before;
  }

  public void setBefore(String before) {
    this.before = before;
  }

  @Override
  public String toString() {
    return "EvaluationCondition{"
        + "key='"
        + key
        + '\''
        + ", type='"
        + type
        + '\''
        + ", equals='"
        + equals
        + '\''
        + ", notEquals='"
        + notEquals
        + '\''
        + ", oneOf="
        + oneOf
        + ", notOneOf="
        + notOneOf
        + ", withinDays="
        + withinDays
        + ", beyondDays="
        + beyondDays
        + ", contains='"
        + contains
        + '\''
        + ", notContains='"
        + notContains
        + '\''
        + ", after='"
        + after
        + '\''
        + ", before='"
        + before
        + '\''
        + '}';
  }
}
