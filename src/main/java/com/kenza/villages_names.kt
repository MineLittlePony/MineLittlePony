package com.kenza

object PonyNames {

    fun generateFirstName(): String = PONY_FIRST_NAMES[random(0, PONY_FIRST_NAMES.size - 1)]
    fun generateSecondName(): String = PONY_SECOND_NAMES[random(0, PONY_SECOND_NAMES.size - 1)]

}

fun String.upperFirstLetter(): String {
    var string = this
    if (string.contains(":")) {
        string = string.substring(string.indexOf(":") + 1)
    }
    if (string.contains("_")) {
        string = string.substring(string.indexOf("_") + 1)
    }
    val builder = StringBuilder(string)
    builder.setCharAt(0, Character.toUpperCase(string[0]))
    return builder.toString()
}

val PONY_FIRST_NAMES = listOf<String>(
//    #food
    "Apple", "Baked", "Banana", "Berry", "Candy", "Caramel", "Carrot",
    "Cherry", "Cheese", "Cinnamon", "Cookie", "Cream", "Flurry", "Grape", "Half Baked", "Lemon",
    "Peachy", "Pumpkin", "Strawberry", "Sugar", "Sweet", "Sweetie",
//    #jewelry and fancy
    "Amethyst", "Diamond", "Emerald", "Ruby", "Golden", "Goldie", "Silver", "Jewel",
    "Royal", "Gala", "Fancy", "Prism", "Swanky", "Opal",
//    #color and light
    "Blue", "Blues", "Cerulean", "Green", "Lavender", "Lilac", "Periwinkle", "Pinkie", "Purple", "Red",
    "Rainbow", "Neon",
    "Bright", "Starlight", "Sunlight", "Sunny", "Sunset", "Sunshine", "Sunshower", "Twilight",
//    #misc
    "Babs", "Bae", "Beauty", "Big", "Big Daddy", "Charity", "Cloud", "Cloudy", "Cotton",
    "Daisy", "Electric", "Feldspar", "Granite", "Fluffy", "Hayseed", "Holly", "Igneous Rock", "Limestone",
    "Lotus", "Lucky", "Lyra", "Merry", "Perfect", "Pound", "Magnet", "Marble", "Maud",
    "Stormy", "Tall", "Tender", "Top", "Vidala", "Golden", "Golden", "Golden", "Golden", "Golden",
)

val PONY_SECOND_NAMES = listOf<String>(
//    #food
    "Apple",
    "Apples",
    "Applesauce",
    "Berry",
    "Cake",
    "Cider",
    "Cinnamon",
    "Crème",
    "Delicious",
    "Delight",
    "Dumpling",
    "Fritter",
    "Grape",
    "Harvest",
    "Honey",
    "Jellius",
    "Mint",
    "Munchies",
    "Orange",
    "Peachbottom",
    "Pie",
    "Puff",
    "Sandwich",
    "Spices",
    "Strudel",
    "Sweet",
    "Tart",
    "Truffle",
    "Waffle",
    "Wheat",
//    #jewelry and fancy
    "Brass",
    "Brioche",
    "Bullion",
    "Gala",
    "Gem",
    "Jewel",
    "Jubilee",
    "Tiara",
    "Velvet",
    "Rich",
//    #color and light
    "Flare",
    "Lights",
    "Luster",
    "Pink",
    "Radiance",
    "Rays",
    "Shimmer",
    "Shine",
    "Sparkle",
    "Sunrise",
//    #pun
    "Fetlock",
    "Flanks",
    "Letrotski",
    "Harshwhinny",
    "Hooffield",
    "Horseshoepin",
    "Mare",
    "Maresbury",
    "Nandermane",
//    #misc
    "Appleby",
    "Belle",
    "Bloom",
    "Blossom",
    "Bulb",
    "Bumpkin",
    "Chaser",
    "Clover",
    "Cobbler",
    "Crisp",
    "Dash",
    "Daze",
    "Dreams",
    "Drops",
    "Fizzy",
    "Flora",
    "Fluff",
    "Frames",
    "Gavel",
    "Glider",
    "Heart",
    "Hearts",
    "Heartstrings",
    "Hugger",
    "Joy",
    "Kindheart",
    "Leaves",
    "McColt",
    "McIntosh",
    "Melody",
    "Moon",
    "Noteworthy",
    "Pace",
    "Pansy",
    "Pants",
    "Petals",
    "Pin",
    "Quartz",
    "Redheart",
    "Ribbon",
    "Riff",
    "Rose",
    "Seed",
    "Shill",
    "Shot",
    "Skies",
    "Sky",
    "Smile",
    "Smiles",
    "Splash",
    "Split",
    "Spoon",
    "Tooth",
    "Top",
    "Twist",
    "Valet",
    "Wave",
    "Wig"
)