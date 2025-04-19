---
navigation:
  title: MEGA Storage
  icon: item_storage_cell_256m
  parent: index.md
  position: 010
categories:
  - megacells
item_ids:
  - cell_component_1m
  - cell_component_4m
  - cell_component_16m
  - cell_component_64m
  - cell_component_256m
  - mega_item_cell_housing
  - mega_fluid_cell_housing
  - mega_chemical_cell_housing
  - mega_source_cell_housing
  - mega_mana_cell_housing
  - mega_experience_cell_housing
  - item_storage_cell_1m
  - item_storage_cell_4m
  - item_storage_cell_16m
  - item_storage_cell_64m
  - item_storage_cell_256m
  - fluid_storage_cell_1m
  - fluid_storage_cell_4m
  - fluid_storage_cell_16m
  - fluid_storage_cell_64m
  - fluid_storage_cell_256m
  - chemical_storage_cell_1m
  - chemical_storage_cell_4m
  - chemical_storage_cell_16m
  - chemical_storage_cell_64m
  - chemical_storage_cell_256m
  - source_storage_cell_1m
  - source_storage_cell_4m
  - source_storage_cell_16m
  - source_storage_cell_64m
  - source_storage_cell_256m
  - mana_storage_cell_1m
  - mana_storage_cell_4m
  - mana_storage_cell_16m
  - mana_storage_cell_64m
  - mana_storage_cell_256m
  - experience_storage_cell_1m
  - experience_storage_cell_4m
  - experience_storage_cell_16m
  - experience_storage_cell_64m
  - experience_storage_cell_256m
  - portable_item_cell_1m
  - portable_item_cell_4m
  - portable_item_cell_16m
  - portable_item_cell_64m
  - portable_item_cell_256m
  - portable_fluid_cell_1m
  - portable_fluid_cell_4m
  - portable_fluid_cell_16m
  - portable_fluid_cell_64m
  - portable_fluid_cell_256m
  - portable_chemical_cell_1m
  - portable_chemical_cell_4m
  - portable_chemical_cell_16m
  - portable_chemical_cell_64m
  - portable_chemical_cell_256m
  - portable_source_cell_1m
  - portable_source_cell_4m
  - portable_source_cell_16m
  - portable_source_cell_64m
  - portable_source_cell_256m
  - portable_mana_cell_1m
  - portable_mana_cell_4m
  - portable_mana_cell_16m
  - portable_mana_cell_64m
  - portable_mana_cell_256m
  - portable_experience_cell_1m
  - portable_experience_cell_4m
  - portable_experience_cell_16m
  - portable_experience_cell_64m
  - portable_experience_cell_256m
---

# MEGA Cells: Storage

(TODO: Game scene showing a drive with some M-tier cells in it)

## Storage Cells

<Row>
  <ItemImage id="item_storage_cell_1m" scale="2" />
  <ItemImage id="item_storage_cell_4m" scale="2" />
  <ItemImage id="item_storage_cell_16m" scale="2" />
  <ItemImage id="item_storage_cell_64m" scale="2" />
  <ItemImage id="item_storage_cell_256m" scale="2" />
</Row>

As mentioned [earlier](index.md), the Accumulation Processor serves as the first step towards putting together any MEGA
infrastructure, and this includes every higher tier storage cell. With this processor, a 256k Storage Component can be
taken even further, from **1M** (equivalent to "1024k") onwards to the highest M tier of 256M — over *one thousand*
times higher in capacity than 256k.

<RecipeFor id="cell_component_1m" />
<RecipeFor id="cell_component_4m" />
<RecipeFor id="cell_component_16m" />
<RecipeFor id="cell_component_64m" />
<RecipeFor id="cell_component_256m" />

Of course, more capable storage will require a more capable housing to boot, which is where some more Sky Steel comes in
to craft an item cell housing for your new M-tier components.

<Row>
  <RecipeFor id="mega_item_cell_housing" />
  <Recipe id="cells/standard/item_storage_cell_1m" />
  <Recipe id="cells/standard/item_storage_cell_1m_with_housing" />
</Row>

For fluids and everything inbetween, there are also dedicated housings. As it turns out, Sky Stone is powerful enough
that it can alloy with some other metals to also form the appropriate cells, such as copper to make fluid cell housings
out of **Sky Bronze**. Even outside of this guide, whatever you can think of that may be supported by a dedicated cell
type, MEGA can (likely) also accommodate for with its own housing.

<Row>
  <Recipe id="transform/sky_bronze_ingot" />
  <RecipeFor id="mega_fluid_cell_housing" />
</Row>

## Portable Cells

MEGA also provides portable versions of all its cells just as AE2 itself does, though the increased capacity of these
cells will demand a fair bit more energy. As such, note that these are crafted with a
<ItemLink id="ae2:dense_energy_cell" /> as opposed to a regular Energy Cell.

<Row>
  <RecipeFor id="portable_item_cell_1m" />
</Row>
