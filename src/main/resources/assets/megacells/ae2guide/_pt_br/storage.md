---
navigation:
  title: Armazenamento MEGA
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
  - sky_bronze_ingot
  - sky_bronze_block
  - sky_osmium_ingot
  - sky_osmium_block
---

# MEGA Cells: Armazenamento

<GameScene zoom="8" background="transparent">
  <ImportStructure src="assets/assemblies/drive_cells.snbt" />
  <IsometricCamera yaw="195" pitch="10" />
</GameScene>

## [Células de Armazenamento](ae2:items-blocks-machines/storage_cells.md) MEGA

<Row>
  <ItemImage id="mega_item_cell_housing" scale="4" />
  <ItemImage id="item_storage_cell_1m" scale="4" />
  <ItemImage id="item_storage_cell_4m" scale="4" />
  <ItemImage id="item_storage_cell_16m" scale="4" />
  <ItemImage id="item_storage_cell_64m" scale="4" />
  <ItemImage id="item_storage_cell_256m" scale="4" />
</Row>

Como mencionado anteriormente, o <ItemLink id="megacells:accumulation_processor" /> serve como o primeiro passo para montar
qualquer infraestrutura MEGA, e isso inclui, para começar, seus próprios tiers mais altos de células de armazenamento. Com este
processador, um <ItemLink id="ae2:cell_component_256k" /> pode ser levado *ainda mais além*, de **1M** (equivalente a
"1024k") em diante até o tier M mais alto de **256M** — mais de *mil* vezes maior em capacidade que um de 256k.

<RecipeFor id="cell_component_1m" />
<RecipeFor id="cell_component_4m" />
<RecipeFor id="cell_component_16m" />
<RecipeFor id="cell_component_64m" />
<RecipeFor id="cell_component_256m" />

Claro, um armazenamento mais potente exigirá um invólucro mais potente para acompanhar, e é aí que um pouco mais de Sky Steel entra
para criar um invólucro de célula de item para seus novos componentes de tier M.

<Row>
  <RecipeFor id="mega_item_cell_housing" />
  <Recipe id="cells/standard/item_storage_cell_1m" />
  <Recipe id="cells/standard/item_storage_cell_1m_with_housing" />
</Row>

Para fluidos e todo o resto, também existem invólucros dedicados. Acontece que a Sky Stone é poderosa o suficiente para
formar ligas com outros metais para também criar as células apropriadas, como com cobre para fazer invólucros de célula de fluido
de **Sky Bronze**. Mesmo fora deste guia, qualquer coisa que você possa imaginar pode ser suportada pela MEGA com uma célula dedicada
com seu próprio tipo de invólucro.

<Row>
  <ItemImage id="sky_bronze_ingot" scale="4" />
  <ItemImage id="mega_fluid_cell_housing" scale="4" />
  <ItemImage id="fluid_storage_cell_1m" scale="4" />
  <ItemImage id="fluid_storage_cell_4m" scale="4" />
  <ItemImage id="fluid_storage_cell_16m" scale="4" />
  <ItemImage id="fluid_storage_cell_64m" scale="4" />
  <ItemImage id="fluid_storage_cell_256m" scale="4" />
</Row>

<Row>
  <Recipe id="transform/sky_bronze_ingot" />
  <RecipeFor id="mega_fluid_cell_housing" />
</Row>

## [Células Portáteis](ae2:items-blocks-machines/storage_cells.md#portable-item-storage) MEGA

A MEGA também fornece versões portáteis de todas as suas células, assim como o próprio AE2, embora a capacidade aumentada dessas
células exija um pouco mais de energia. Sendo assim, note que elas são criadas com uma
<ItemLink id="ae2:dense_energy_cell" /> em vez de uma <ItemLink id="ae2:energy_cell" /> comum.

Embora essas células portáteis também suportem a gama completa de [aprimoramentos](ae2:items-blocks-machines/upgrade_cards.md) que
as células portáteis ME comuns suportam, sua bateria aumentada e sua fome geral por energia significam que o
<ItemLink id="ae2:energy_card" /> comum não é forte o *suficiente* para suportá-las. Para este propósito, apenas a
<ItemLink id="megacells:greater_energy_card" /> servirá.

<Row>
  <RecipeFor id="portable_item_cell_1m" />
</Row>