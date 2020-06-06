#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

# Script to run all resource generation

import assets.metals
import assets.stones
import data.item_heats
import data.metal_items
import data.metals
import data.ore_veins
import data.rocks
import lang.metals
import lang.misc
import recipes.collapse
import vanilla.tags
from mcresources import ResourceManager, clean_generated_resources


def main():
    rm = ResourceManager('tfc', resource_dir='../src/main/resources')
    clean_generated_resources('/'.join(rm.resource_dir))

    data.ore_veins.generate(rm)
    data.rocks.generate(rm)
    data.metals.generate(rm)
    data.item_heats.generate(rm)
    data.metal_items.generate(rm)

    recipes.collapse.generate(rm)

    assets.stones.generate(rm)
    assets.metals.generate(rm)
    lang.metals.generate(rm)
    lang.misc.generate(rm)
    
    vanilla.tags.generate(rm)

    rm.flush()


if __name__ == '__main__':
    main()