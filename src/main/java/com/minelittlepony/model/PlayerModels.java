package com.minelittlepony.model;

public class PlayerModels {
  public static final PlayerModels
    HUMAN = new PlayerModels("default", "slim", PMAPI.human, PMAPI.humanSmall),
    PONY = new PlayerModels("pony", "slimpony", PMAPI.pony, PMAPI.ponySmall);
  
  private final PlayerModel normal, slim;
  
  private final String normalKey, slimKey;
  
  public PlayerModels(String normalKey, String slimKey, PlayerModel normal, PlayerModel slim) {
    this.normalKey = normalKey;
    this.slimKey = slimKey;
    
    this.normal = normal;
    this.slim = slim;
  }
  
  public PlayerModel getModel(boolean slim) {
      return slim ? this.slim : this.normal; 
  }
  
  public String getId(boolean useSlimArms) {
      return useSlimArms ? slimKey : normalKey;
  }
}
