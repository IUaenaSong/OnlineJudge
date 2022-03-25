<template>
  <comment :cid="$route.params.contestID"></comment>
</template>
<script>
import comment from '@/components/oj/comment/Comment.vue';
export default {
  name: 'ContestComment',
  components: {
    comment,
  },
};
</script>
