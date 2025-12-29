// 基于 Thymeleaf 的模板，我们假设后端返回的数据已注入到页面中
// 这里模拟与后端交互（实际应由 Spring MVC 控制器提供接口）

function addToCart(productId) {
  fetch('/api/cart/add', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ productId, quantity: 1 })
  })
  .then(res => res.json())
  .then(data => {
    if (data.message === 'success') {
      // 更新购物车数量（可重新请求 /api/cart/count 或直接更新）
      alert('已添加到购物车！');
    }
  })
  .catch(err => {
    console.error('添加失败:', err);
    alert('添加失败');
  });
}