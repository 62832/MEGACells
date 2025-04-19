---
navigation:
  title: MEGA Auto-Crafting
  icon: 256m_crafting_storage
  parent: index.md
  position: 020
categories:
  - megacells
item_ids:
  - mega_crafting_unit
  - 1m_crafting_storage
  - 4m_crafting_storage
  - 16m_crafting_storage
  - 64m_crafting_storage
  - 256m_crafting_storage
  - mega_crafting_accelerator
  - mega_pattern_provider
  - cable_mega_pattern_provider
---

# MEGA Cells: Auto-Crafting

(TODO: Game scene showing a fully-formed CPU multiblock)

## Crafting Storage

<Row>
  <ItemImage id="1m_crafting_storage" scale="3" />
  <ItemImage id="4m_crafting_storage" scale="3" />
  <ItemImage id="16m_crafting_storage" scale="3" />
  <ItemImage id="64m_crafting_storage" scale="3" />
  <ItemImage id="256m_crafting_storage" scale="3" />
  <ItemImage id="mega_crafting_accelerator" scale="3" />
</Row>

As with storage cells, MEGA also provides its larger tiers of storage for
[crafting CPUs](ae2:ae2-mechanics/autocrafting.md). Although these, too, require their own dedicated crafting unit to
cope with the increased capacity, these will still handle even the biggest crafting jobs easily with more memory.

<RecipeFor id="mega_crafting_unit" />

<RecipeFor id="1m_crafting_storage" />
<RecipeFor id="4m_crafting_storage" />
<RecipeFor id="16m_crafting_storage" />
<RecipeFor id="64m_crafting_storage" />
<RecipeFor id="256m_crafting_storage" />

As an added bonus, MEGA also provides its own equivalent to the <ItemLink id="ae2:crafting_accelerator" />, though with
the advantage of providing not one, but *FOUR* coprocessing threads in the space of each single added coprocessor block.

<RecipeFor id="mega_crafting_accelerator" />

## MEGA Pattern Provider

<Row>
  <ItemImage id="mega_pattern_provider" scale="4" />
  <ItemImage id="cable_mega_pattern_provider" scale="4" />
</Row>

Serving as a companion to the <ItemLink id="ae2:pattern_provider" />, the MEGA Pattern
Provider maintains the tradition of providing larger variants of appropriate AE2 devices by doubling the pattern
capacity, allowing for a total of 18 patterns to be held within it. This does, however, come with the trade-off of only
being able to hold **processing patterns**, meaning it will not quite work with the Molecular Assembler.

<Row>
  <RecipeFor id="mega_pattern_provider" />
  <RecipeFor id="cable_mega_pattern_provider" />
</Row>
