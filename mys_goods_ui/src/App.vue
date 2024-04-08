<template>
  <router-view />
</template>
<script>
  import { defineComponent } from "vue";
  export default defineComponent({
    setup() {
      const debounce = (fn, delay) => {
        let timer = null;
        return function () {
          let context = this;
          let args = arguments;
          clearTimeout(timer);
          timer = setTimeout(function () {
            fn.apply(context, args);
          }, delay);
        };
      };

      const _ResizeObserver = window.ResizeObserver;

      window.ResizeObserver = class ResizeObserver extends _ResizeObserver {
        constructor(callback) {
          callback = debounce(callback, 16);
          super(callback);
        }
      };
    },
  });
</script>