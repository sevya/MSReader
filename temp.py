import colorsys,sys,re
from pymol import cmd
cmd.show_as("cartoon")


def hsv_to_rgb(hsv):
        h = float(hsv[0])
        s = float(hsv[1])
        v = float(hsv[2])
        if( s == 0 ) :
                #achromatic (grey)
                r = g = b = v
        else:
                # sector 0 to 5
                h = h/60.            
                i = int(h)
                f = h - i                       # factorial part of h
                #print h,i,f
                p = v * ( 1 - s )
                q = v * ( 1 - s * f )
                t = v * ( 1 - s * ( 1 - f ) )
                if i == 0:
                        (r,g,b) = (v,t,p)
                elif i == 1:
                        (r,g,b) = (q,v,p)
                elif i == 2:
                        (r,g,b) = (p,v,t)
                elif i == 3:
                        (r,g,b) = (p,q,v)
                elif i == 4:
                        (r,g,b) = (t,p,v)
                elif i == 5:
                        (r,g,b) = (v,p,q)
                else:
                        (r,g,b) = (v,v,v)
                        print "error, i not equal 1-5"
        return [r,g,b]


def color_as(col, str):
        hsv = (col, 1, 1)
        rgb = hsv_to_rgb(hsv)
        cmd.set_color("color"+str, rgb)
        cmd.color("color"+str, str)
        cmd.disable(str)


cmd.color("white", "resi 2171");
cmd.color("white", "resi 2172");
cmd.color("white", "resi 2173");
cmd.color("white", "resi 2174");
cmd.color("white", "resi 2175");
cmd.color("white", "resi 2176");
cmd.color("white", "resi 2177");
cmd.color("white", "resi 2178");
cmd.color("white", "resi 2179");
cmd.color("white", "resi 2180");
cmd.color("white", "resi 2181");
cmd.color("white", "resi 2182");
color_as(70, "resi 2183");
color_as(70, "resi 2184");
color_as(70, "resi 2185");
color_as(70, "resi 2186");
color_as(70, "resi 2187");
color_as(70, "resi 2188");
color_as(48, "resi 2189");
color_as(59, "resi 2190");
color_as(59, "resi 2191");
color_as(56, "resi 2192");
color_as(59, "resi 2193");
color_as(59, "resi 2194");
color_as(32, "resi 2195");
color_as(31, "resi 2196");
color_as(31, "resi 2197");
color_as(31, "resi 2198");
color_as(31, "resi 2199");
color_as(0, "resi 2200");
color_as(0, "resi 2201");
color_as(0, "resi 2202");
color_as(0, "resi 2203");
color_as(0, "resi 2204");
color_as(0, "resi 2205");
color_as(0, "resi 2206");
color_as(0, "resi 2207");
color_as(0, "resi 2208");
color_as(0, "resi 2209");
color_as(0, "resi 2210");
color_as(0, "resi 2211");
color_as(0, "resi 2212");
color_as(0, "resi 2213");
color_as(0, "resi 2214");
color_as(0, "resi 2215");
color_as(0, "resi 2216");
color_as(30, "resi 2217");
color_as(30, "resi 2218");
color_as(30, "resi 2219");
color_as(91, "resi 2220");
color_as(91, "resi 2221");
color_as(91, "resi 2222");
color_as(91, "resi 2223");
color_as(91, "resi 2224");
color_as(91, "resi 2225");
color_as(91, "resi 2226");
color_as(91, "resi 2227");
color_as(83, "resi 2228");
color_as(144, "resi 2229");
color_as(161, "resi 2230");
color_as(161, "resi 2231");
color_as(161, "resi 2232");
color_as(169, "resi 2233");
color_as(157, "resi 2234");
color_as(64, "resi 2235");
color_as(64, "resi 2236");
color_as(64, "resi 2237");
color_as(64, "resi 2238");
color_as(64, "resi 2239");
color_as(64, "resi 2240");
color_as(64, "resi 2241");
color_as(64, "resi 2242");
color_as(73, "resi 2243");
color_as(73, "resi 2244");
color_as(73, "resi 2245");
color_as(73, "resi 2246");
color_as(79, "resi 2247");
color_as(79, "resi 2248");
color_as(79, "resi 2249");
color_as(79, "resi 2250");
color_as(134, "resi 2251");
color_as(134, "resi 2252");
color_as(162, "resi 2253");
color_as(162, "resi 2254");
color_as(162, "resi 2255");
color_as(160, "resi 2256");
color_as(134, "resi 2257");
color_as(134, "resi 2258");
color_as(134, "resi 2259");
color_as(112, "resi 2260");
color_as(115, "resi 2261");
color_as(72, "resi 2262");
color_as(72, "resi 2263");
color_as(72, "resi 2264");
color_as(72, "resi 2265");
color_as(72, "resi 2266");
color_as(72, "resi 2267");
color_as(67, "resi 2268");
color_as(67, "resi 2269");
color_as(67, "resi 2270");
color_as(59, "resi 2271");
color_as(59, "resi 2272");
color_as(59, "resi 2273");
color_as(59, "resi 2274");
color_as(93, "resi 2275");
color_as(93, "resi 2276");
color_as(93, "resi 2277");
color_as(93, "resi 2278");
color_as(93, "resi 2279");
color_as(93, "resi 2280");
color_as(93, "resi 2281");
color_as(93, "resi 2282");
color_as(93, "resi 2283");
color_as(93, "resi 2284");
color_as(93, "resi 2285");
color_as(93, "resi 2286");
color_as(93, "resi 2287");
color_as(9, "resi 2288");
color_as(9, "resi 2289");
color_as(9, "resi 2290");
color_as(9, "resi 2291");
color_as(9, "resi 2292");
color_as(9, "resi 2293");
color_as(9, "resi 2294");
color_as(9, "resi 2295");
color_as(56, "resi 2296");
color_as(56, "resi 2297");
color_as(56, "resi 2298");
color_as(63, "resi 2299");
color_as(63, "resi 2300");
color_as(63, "resi 2301");
color_as(63, "resi 2302");
color_as(60, "resi 2303");
color_as(60, "resi 2304");
color_as(60, "resi 2305");
color_as(104, "resi 2306");
color_as(104, "resi 2307");
color_as(104, "resi 2308");
color_as(104, "resi 2309");
color_as(104, "resi 2310");
color_as(104, "resi 2311");
color_as(104, "resi 2312");
color_as(95, "resi 2313");
color_as(87, "resi 2314");
color_as(87, "resi 2315");
color_as(87, "resi 2316");
color_as(87, "resi 2317");
color_as(87, "resi 2318");
color_as(79, "resi 2319");
color_as(91, "resi 2320");
color_as(91, "resi 2321");
cmd.color("white", "resi 2322");
cmd.color("white", "resi 2323");
cmd.color("white", "resi 2324");
cmd.color("white", "resi 2325");
cmd.color("white", "resi 2326");
cmd.color("white", "resi 2327");
cmd.color("white", "resi 2328");
cmd.color("white", "resi 2329");
cmd.color("white", "resi 2330");
cmd.color("white", "resi 2331");
