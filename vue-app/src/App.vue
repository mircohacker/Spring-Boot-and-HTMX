<!--
A nested tree component that recursively renders itself.
You can double click on an item to turn it into a folder.

Stolen from [here](https://vuejs.org/examples/#tree)
-->

<script setup>
import { ref } from 'vue'
import TreeItem from '@/components/TreeItem.vue'

const treeData = ref({
  name: 'My Tree',
  children: [
    { name: 'hello (try a double click)' },
    { name: 'world' },
    {
      name: 'child folder',
      children: [
        {
          name: 'child folder',
          children: [{ name: 'hello' }, { name: 'world' }],
        },
        { name: 'hello' },
        { name: 'world' },
        {
          name: 'child folder',
          children: [{ name: 'hello' }, { name: 'world' }],
        },
      ],
    },
  ],
})
</script>

<template>
  <ul>
    <TreeItem class="item" :model="treeData"></TreeItem>
  </ul>

</template>

<style>
.item {
  cursor: pointer;
  line-height: 1.5;
}
.bold {
  font-weight: bold;
}
</style>
