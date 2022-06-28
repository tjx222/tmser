<% if (get_config('toc') === true && (post.layout === 'page' || post.layout === 'post')) {
function buildToc(toc) {
    let result = '';
    if (toc.hasOwnProperty('id') && toc.hasOwnProperty('index') && toc.hasOwnProperty('text')) {
        result += `<li>
        <a class="is-flex" href="#${toc.id}">
        <span class="has-mr-6">${toc.index}</span>
        <span>${toc.text}</span>
        </a>`;
    }
    let keys = Object.keys(toc);
    keys.indexOf('id') > -1 && keys.splice(keys.indexOf('id'), 1);
    keys.indexOf('text') > -1 && keys.splice(keys.indexOf('text'), 1);
    keys.indexOf('index') > -1 && keys.splice(keys.indexOf('index'), 1);
    keys = keys.map(k => parseInt(k)).sort((a, b) => a - b);
    if (keys.length > 0) {
        result += '<ul class="menu-list">';
        for (let i of keys) {
            result += buildToc(toc[i]);
        }
        result += '</ul>';
    }
    if (toc.hasOwnProperty('id') && toc.hasOwnProperty('index') && toc.hasOwnProperty('text')) {
        result += '</li>';
    }
    return result;
}
%>
<div class="card widget" id="toc">
    <div class="card-content">
        <div class="menu">
            <h3 class="menu-label">
                <%= _p('widget.catalogue', Infinity) %>
            </h3>
            <%- buildToc(_toc(post.content)) %>
        </div>
    </div>
</div>
<% } %>