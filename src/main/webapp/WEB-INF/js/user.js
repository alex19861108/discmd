/**
 * Created by alex on 2016/11/4.
 */

var User = function(){
    this.engine = undefined;
    this.selector = undefined;
    this.remoteUrl = undefined;
    this.display = undefined;
    this.name = undefined;
};

User.prototype.init = function() {
    this.engine = new Bloodhound({
        datumTokenizer: function (datum) {
            return Bloodhound.tokenizers.whitespace(datum.value);
        },
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        remote: {
            url: this.remoteUrl,
            wildcard: "%QUERY",
            rateLimitBy: 100
        }
    });
    this.engine.initialize();
};

User.prototype.tokenfield = function() {
    this.selector.tokenfield({
        limit: 0,
        minLength: 0,
        minWidth: 60,
        typeahead: [{
            hint: true, // 设置为false时，typeahead 将不会显示hint。默认为true.
            highlight: true,    // 设置为true时，当建议渲染时，在文本节点中，匹配查询模式的文字将被带有tt-highlight class的strong元素包裹。默认设置为false。
            minLength: 1    // 推荐引擎开始渲染所需要的最小字符。默认为 1
        }, {
            source: this.engine.ttAdapter(),
            display: this.display,
            name: this.name,
            templates: {
                suggestion: function (data) {
                    // `suggestion` object passed at `engine`
                    // return suggestion element to display
                    var _suggestion = "<div>"
                        + data.realname
                        + "("
                        + data.username + ")</div>";
                    return _suggestion
                }
            }
        }],
        createTokensOnBlur: true,
        delimiter: ',',
        beautify: true,
        inputType: 'text'
    }).on("tokenfield:createtoken", function(event) {
        var existingTokens = $(this).tokenfield('getTokens');
        $.each(existingTokens, function(index, token) {
            if (token.value === event.attrs.value)
                event.preventDefault();
        });
    }).on("tokenfield:createdtoken", function(event) {
        console.log(event);
        var userIds = $("#userIds").val();
        if (userIds != "") {
            $("#userIds").val(userIds + ',');
        } else {
            $("#userIds").val(userIds);
        }
    });
};
