

function enableTooltips(id){

var links,i,h;

if(!document.getElementById || !document.getElementsByTagName) return;

AddCss();

h=document.createElement("span");

h.id="btc";

h.setAttribute("id","btc");

h.style.position="absolute";

document.getElementsByTagName("body")[0].appendChild(h);

if(id==null) links=document.getElementsByTagName("a");

else links=document.getElementById(id).getElementsByTagName("a");

for(i=0;i<links.length;i++){

    Prepare(links[i]);

    }

}



function Prepare(el){

var tooltip,t,b,s;

t=el.getAttribute("title");

if(t==null || t.length==0) return;

el.removeAttribute("title");

tooltip=CreateEl("span","tooltip");

s=CreateEl("span","top");

s.appendChild(document.createTextNode(t));

tooltip.appendChild(s);

b=CreateEl("b","bottom");

l="";

b.appendChild(document.createTextNode(l));

tooltip.appendChild(b);

setOpacity(tooltip);

el.tooltip=tooltip;

el.onmouseover=showTooltip;

el.onmouseout=hideTooltip;

el.onmousemove=Locate;

}



function showTooltip(e){

document.getElementById("btc").appendChild(this.tooltip);

Locate(e);

}



function hideTooltip(e){

var d=document.getElementById("btc");

if(d.childNodes.length>0) d.removeChild(d.firstChild);

}



function setOpacity(el){

el.style.filter="alpha(opacity:95)";

el.style.KHTMLOpacity="0.95";

el.style.MozOpacity="0.95";

el.style.opacity="0.95";

}



function CreateEl(t,c){

var x=document.createElement(t);

x.className=c;

x.style.display="block";

return(x);

}



function AddCss(){

var l=CreateEl("link");

l.setAttribute("type","text/css");

l.setAttribute("rel","stylesheet");

l.setAttribute("href","doc.css");

l.setAttribute("media","screen");

document.getElementsByTagName("head")[0].appendChild(l);

}



function Locate(e){

var posx=0,posy=0;

if(e==null) e=window.event;

if(e.pageX || e.pageY){

    posx=e.pageX; posy=e.pageY;

    }

else if(e.clientX || e.clientY){

    if(document.documentElement.scrollTop){

        posx=e.clientX+document.documentElement.scrollLeft;

        posy=e.clientY+document.documentElement.scrollTop;

        }

    else{

        posx=e.clientX+document.body.scrollLeft;

        posy=e.clientY+document.body.scrollTop;

        }

    }

document.getElementById("btc").style.top=(posy+15)+"px";

document.getElementById("btc").style.left=(posx+12)+"px";

}





function toggleDisplay(e){

    imgX="imagePM"+e;

    tableX="table"+e;

    imageX="imageX"+e;

    tableLink="tableHref"+e;

    imageXval=eval("imageX"+e);

    element = document.getElementById(tableX).style;

     if (element.display=='none') {

        element.display='block';

        }

     else {

        element.display='none';

        }
        currImgSrc = document.getElementById(imgX).src;
        if (imageXval=='plus') {
            //document.getElementById(imgX).src='webdesign/other/im/e-c-plus.gif';
            document.getElementById(imgX).src=currImgSrc.replace("minus", "plus");
            eval("imageX"+e+"='minus';");

            document.getElementById(tableLink).title='';

        }

        else {
            //document.getElementById(imgX).src='webdesign/other/im/e-c-minus.gif';
            document.getElementById(imgX).src=currImgSrc.replace("plus", "minus");
            eval("imageX"+e+"='plus';");

            document.getElementById(tableLink).title='';

        }

    }

	

	function toggleDisplayltwo(e){

    imgX="imagePM"+e;

    tableX="table"+e;

    imageX="imageX"+e;

    tableLink="tableHref"+e;

    imageXval=eval("imageX"+e);

    element = document.getElementById(tableX).style;

     if (element.display=='none') {

        element.display='block';

        }

     else {

        element.display='none';

        }
        
        currImgSrc = document.getElementById(imgX).src;      
        if (imageXval=='plus') {
            
            //document.getElementById(imgX).src='webdesign/other/im/more.png';
            document.getElementById(imgX).src=currImgSrc.replace("less", "more");
            eval("imageX"+e+"='minus';");

            document.getElementById(tableLink).title='';

        }

        else {
            document.getElementById(imgX).src=currImgSrc.replace("more", "less");
            //document.getElementById(imgX).src='webdesign/other/im/less.png';

            eval("imageX"+e+"='plus';");

            document.getElementById(tableLink).title='';

        }

    }

	
	function toggleDisplaylthree(e){

    imgX="imagePM"+e;

    tableX="table"+e;

    imageX="imageX"+e;

    tableLink="tableHref"+e;

    imageXval=eval("imageX"+e);

    element = document.getElementById(tableX).style;

     if (element.display=='none') {

        element.display='block';

        }

     else {

        element.display='none';

        }

        if (imageXval=='plus') {

            document.getElementById(imgX).src='webdesign/other/im/options-plus.gif';

            eval("imageX"+e+"='minus';");

            document.getElementById(tableLink).title='';

        }

        else {

            document.getElementById(imgX).src='webdesign/other/im/options-minus.gif';

            eval("imageX"+e+"='plus';");

            document.getElementById(tableLink).title='';

        }

    }
	



function MM_swapImgRestore() { //v3.0

  var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;

}



function MM_preloadImages() { //v3.0

  var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();

    var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)

    if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}

}



function MM_findObj(n, d) { //v4.01

  var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {

    d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}

  if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];

  for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);

  if(!x && d.getElementById) x=d.getElementById(n); return x;

}



function MM_swapImage() { //v3.0

  var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)

   if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}

}



