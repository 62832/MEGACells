---
navigation:
  title: Fabricação Automática MEGA
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
  - mega_crafting_monitor
  - mega_pattern_provider
  - cable_mega_pattern_provider
---

# MEGA Cells: Fabricação Automática

<GameScene zoom="6" background="transparent">
  <ImportStructure src="assets/assemblies/crafting_cpu.snbt" />
  <IsometricCamera yaw="195" pitch="10" />
</GameScene>

## [CPUs de Fabricação](ae2:items-blocks-machines/crafting_cpu_multiblock.md) MEGA

<Row>
  <BlockImage id="mega_crafting_unit" scale="4" />
  <BlockImage id="1m_crafting_storage" scale="4" />
  <BlockImage id="4m_crafting_storage" scale="4" />
  <BlockImage id="16m_crafting_storage" scale="4" />
  <BlockImage id="64m_crafting_storage" scale="4" />
  <BlockImage id="256m_crafting_storage" scale="4" />
</Row>

Assim como nas células de armazenamento, a MEGA também fornece seus tiers maiores de armazenamento para CPUs de Fabricação. Embora estes também exijam
sua própria versão dedicada da <ItemLink id="ae2:crafting_unit" /> para acomodar seu aumento de poder, eles
ainda lidarão facilmente com os maiores trabalhos de Fabricação com mais memória, além de terem uma aparência *muito legal* em
preto.

<RecipeFor id="mega_crafting_unit" />
<RecipeFor id="1m_crafting_storage" />
<RecipeFor id="4m_crafting_storage" />
<RecipeFor id="16m_crafting_storage" />
<RecipeFor id="64m_crafting_storage" />
<RecipeFor id="256m_crafting_storage" />

Como um bônus adicional, a MEGA também fornece seu próprio equivalente ao <ItemLink id="ae2:crafting_accelerator" />, embora com
a vantagem de fornecer não um, mas *QUATRO* threads de coprocessamento no espaço de cada bloco de coprocessador adicionado.

<BlockImage id="mega_crafting_accelerator" scale="4" />
<RecipeFor id="mega_crafting_accelerator" />

E apenas para completar o pacote, também há um equivalente MEGA do <ItemLink id="ae2:crafting_monitor" />.
Este, na verdade, não funciona de maneira diferente do monitor comum, mas serve como um complemento para
as unidades mencionadas para usuários que desejam manter uma consistência estética e o mesmo visual elegante e escuro
em todo o seu multiblock de CPU.

<BlockImage id="mega_crafting_monitor" scale="4" />
<RecipeFor id="mega_crafting_monitor" />

## Fornecedor de Padrões MEGA

<Row>
  <BlockImage id="mega_pattern_provider" scale="4" />
  <GameScene zoom="4" background="transparent">
    <ImportStructure src="assets/assemblies/cable_mega_pattern_provider.snbt" />
  </GameScene>
</Row>

Servindo como um companheiro para o <ItemLink id="ae2:pattern_provider" />, o **Fornecedor de Padrões MEGA** mantém a
tendência de fornecer variantes maiores de dispositivos AE2 apropriados, dobrando a capacidade de padrões, permitindo um total de
18 padrões a serem armazenados e manuseados por ele. Isso, no entanto, vem com a desvantagem de só poder armazenar
[**padrões de processamento**](ae2:items-blocks-machines/patterns.md), então não funcionará muito bem com o
<ItemLink id="ae2:molecular_assembler" />.

<Row>
  <RecipeFor id="mega_pattern_provider" />
  <RecipeFor id="cable_mega_pattern_provider" />
</Row>